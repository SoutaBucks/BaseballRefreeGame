package com.baseballrefree.pitch;

import com.baseballrefree.judgment.Judgment;
import com.baseballrefree.strikezone.StrikeZone;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/* 변수 설명
 * MOUND_TO_PLATE_CM -> 마운드에서 홈플레이트까지의 거리
 * MOUND_TO_HEIGHT_CM -> 투수가 공을 놓는 높이
 * TIME_STEP_SEC -> 궤적을 샘플링하는 시간 간격, 숫자가 작을수록 촘촘해진다
 * BALL_RADIUS_CM -> 공 반지름
 * SWUNG_EXTENT_THRESHOLD -> 스윙 정도 (swingExtent)가 이 값 이상이면 스윙으로 판정
 * NO_SWING -> 노 스윙 기준 값
 * CHECK_SWING_PROBABILITY -> 체크스윙을 할 확률
 * HELD_BACK_EXTENT_MIN, HELD_BACK_EXTENT_MAX -> 노스윙의 경우 범위
 * SWUNG_EXTENT_MIN, SWUNG_EXTENT_MAX -> 스윙 경우의 범위
 * [아래 PLATE_X_SPREAD_CM / PLATE_Y_MIN_CM / PLATE_Y_MAX_CM 공통 설명]
 * 홈 플레이트 도착 지점 (plateX, plateY)을 랜덤으로 뽑는 범위(cm) 골고루 해야 완전히 벗어나는 볼도 나온다
 * PLATE_X_SPREAD_CM -> 홈플레이트 도착 지점의 좌우(X)의 전체 폭, 0을 중심으로 +-35cm
 * PLATE_Y_MIN_CM, PLATE_Y_MAX_CM -> 홈플레이트 도착 지점의 높이 범위
 * RELEASE_X_SPREAD_CM -> 릴리스 지점의 좌우 폭, 0을 중심으로 +-10cm
 * GRAVITY_CM_PER_SEC2 -> 중력가속도(cm/s^2), 포물선 낙차 계산에 사용
 * AIR_DENSITY_KG_PER_CM3 -> 공기 밀도
 * DRAG_COEFFICIENT -> 야구공 항력계수
 * BALL_MASS_KG -> 야구공 질량
 * DRAG_CONSTANT_PER_CM -> 공기저항 상수
 * INTEGRATION_STEP_SEC -> 운동 방정식 수치 적분의 시간 간격
 * AIM_ITERATIONS -> 도착점을 맞추기 위해 초기 속도를 보정하며 다시 던져 보는 횟수
 */
public final class PitchSimulator {
  private static final double MOUND_TO_PLATE_CM = 1844.0;
  private static final double RELEASE_HEIGHT_CM = 180.0;
  private static final double TIME_STEP_SEC = 0.02;
  private static final double BALL_RADIUS_CM = 3.65;
  public static final double SWUNG_EXTENT_THRESHOLD = 0.5;
  private static final double NO_SWING = 0.0;
  private static final double CHECK_SWING_PROBABILITY = 0.35;
  private static final double HELD_BACK_EXTENT_MIN = 0.15;
  private static final double HELD_BACK_EXTENT_MAX = 0.4;
  private static final double SWUNG_EXTENT_MIN = 0.65;
  private static final double SWUNG_EXTENT_MAX = 0.95;
  private static final double PLATE_X_SPREAD_CM = 70.0;
  private static final double PLATE_Y_MIN_CM = 30.0;
  private static final double PLATE_Y_MAX_CM = 140.0;
  private static final double RELEASE_X_SPREAD_CM = 20.0;
  private static final double GRAVITY_CM_PER_SEC2 = 980.0;
  private static final double AIR_DENSITY_KG_PER_CM3 = 1.2e-6;
  private static final double DRAG_COEFFICIENT = 0.35;
  private static final double BALL_MASS_KG = 0.145;
  private static final double DRAG_CONSTANT_PER_CM = dragConstantPerCm();
  private static final double INTEGRATION_STEP_SEC = 0.0005;
  private static final int AIM_ITERATIONS = 8;

  private PitchSimulator() {}

  public static Pitch simulate(StrikeZone zone, Random random) {
    PitchType type = randomPitchType(random);
    double speedKmh = randomSpeedKmh(type, random);
    double releaseSpeedCmPerSec = kmhToCmPerSec(speedKmh);

    double releaseX = randomInRange(random, -RELEASE_X_SPREAD_CM / 2, RELEASE_X_SPREAD_CM / 2);
    double plateX = randomInRange(random, -PLATE_X_SPREAD_CM / 2, PLATE_X_SPREAD_CM / 2);
    double plateY = randomInRange(random, PLATE_Y_MIN_CM, PLATE_Y_MAX_CM);

    List<PitchPoint> trajectory = buildTrajectory(type, releaseSpeedCmPerSec, releaseX, plateX, plateY);

    boolean ballTouchesZone = zone.touches(plateX, plateY, BALL_RADIUS_CM);
    double swingExtent = randomCheckSwingExtent(random);
    boolean batterSwung = swingExtent >= SWUNG_EXTENT_THRESHOLD;

    boolean isStrike = ballTouchesZone || batterSwung;
    Judgment groundTruth = isStrike ? Judgment.STRIKE : Judgment.BALL;

    return new Pitch(type, speedKmh, trajectory, plateX, plateY, groundTruth, zone, swingExtent);
  }

  // 타자가 체크스윙을 하는 경우 절반은 노스윙, 절반은 스윙으로 한다
  // 두 구간 사이를 비워 두어 육안으로 구분 가능하게 한다
  private static double randomCheckSwingExtent(Random random) {
    if (random.nextDouble() >= CHECK_SWING_PROBABILITY) {
      return NO_SWING;
    }
    boolean swung = random.nextBoolean();
    return swung
        ? randomInRange(random, SWUNG_EXTENT_MIN, SWUNG_EXTENT_MAX)
        : randomInRange(random, HELD_BACK_EXTENT_MIN, HELD_BACK_EXTENT_MAX);
  }

  private static PitchType randomPitchType(Random random) {
    PitchType[] types = PitchType.values(); // ENUM의 value들을 배열로 반환
    return types[random.nextInt(types.length)]; // 구종 중에서 랜덤으로 선택
  }

  private static double randomSpeedKmh(PitchType type, Random random) {
    return randomInRange(random, type.minSpeedKmh, type.maxSpeedKmh);
  }

  private static double randomInRange(Random random, double min, double max) {
    return min + random.nextDouble() * (max - min);
  }

  private static double kmhToCmPerSec(double speedKmh) {
    return speedKmh * 1000.0 * 100.0 / 3600.0;
  }

  // 공기저항 상수 k = ρ·Cd·A / (2m) (1/cm). 항력 F = ½·ρ·Cd·A·v² 를 질량으로 나눈 가속도의 계수다.
  private static double dragConstantPerCm() {
    double crossSectionCm2 = Math.PI * BALL_RADIUS_CM * BALL_RADIUS_CM;
    return AIR_DENSITY_KG_PER_CM3 * DRAG_COEFFICIENT * crossSectionCm2 / (2 * BALL_RADIUS_CM);
  }

  // 공기저항만 받는 직선 비행의 도달 시간(초). dv/dt = -k·v² 를 풀면 이동거리 d = ln(1 + k·v0·t) / k 이므로
  // t = (e^(k·d) - 1) / (k·v0). 회전 가속도를 정하기 위한 추정치로만 쓴다(실제 궤적은 수치 적분).
  private static double flightTimeWithDragSec(double releaseSpeedCmPerSec) {
    double kd = DRAG_CONSTANT_PER_CM * MOUND_TO_PLATE_CM;
    return (Math.exp(kd) - 1) / (DRAG_CONSTANT_PER_CM * releaseSpeedCmPerSec);
  }

  // 구종 무브먼트(cm)를 회전(마그누스 힘)에 의한 일정 가속도(cm/s²)로 환산한다.
  // Statcast 무브먼트는 등가속도 모델의 변위 ½·a·T² 로 정의되므로 a = 2·M / T².
  private static double movementToAccelCmPerSec2(double movementCm, double flightTimeSec) {
    return 2 * movementCm / (flightTimeSec * flightTimeSec);
  }

  // 릴리스 속력을 유지하면서 좌우, 상하 성분을 뺀 나머지 전진(홈플레이트 방향) 속력(cm/s)
  private static double forwardSpeedCmPerSec(double releaseSpeedCmPerSec, double vx, double vy) {
    return Math.sqrt(releaseSpeedCmPerSec * releaseSpeedCmPerSec - vx * vx - vy * vy);
  }

  // 목표 도착점(plateX, plateY)에 들어가는 궤적을 만든다(슈팅법).
  // 초기 속도를 추정해 던져 보고, 빗나간 거리 / 비행시간 만큼 초기 속도를 보정해 다시 던지기를 반복한다.
  // releaseSpeedCmPerSec: 릴리스 속력(전광판 구속), releaseX: 릴리스 좌우 위치.
  private static List<PitchPoint> buildTrajectory(PitchType type, double releaseSpeedCmPerSec, double releaseX, double plateX, double plateY) {
    double estimatedFlightSec = flightTimeWithDragSec(releaseSpeedCmPerSec);
    double spinAccelX = movementToAccelCmPerSec2(type.horizontalBreakCm, estimatedFlightSec);
    double spinAccelY = movementToAccelCmPerSec2(-type.extraDropCm, estimatedFlightSec);

    double vx = (plateX - releaseX) / estimatedFlightSec;
    double vy = (plateY - RELEASE_HEIGHT_CM) / estimatedFlightSec;
    List<PitchPoint> trajectory = List.of();
    for (int i = 0; i < AIM_ITERATIONS; i++) {
      double vz = -forwardSpeedCmPerSec(releaseSpeedCmPerSec, vx, vy);
      trajectory = flyBall(releaseX, vx, vy, vz, spinAccelX, spinAccelY);
      PitchPoint arrival = trajectory.get(trajectory.size() - 1);
      vx += (plateX - arrival.x()) / arrival.t();
      vy += (plateY - arrival.y()) / arrival.t();
    }
    // 슈팅 후 남은 미세 오차를 지워 판정 좌표(plateX/plateY)와 정확히 일치시킨다
    PitchPoint arrival = trajectory.get(trajectory.size() - 1);
    trajectory.set(trajectory.size() - 1, new PitchPoint(arrival.t(), plateX, plateY, 0.0));
    return trajectory;
  }

  // 주어진 초기 속도로 공을 던져 홈플레이트 평면(z=0)을 지날 때까지 운동방정식을 적분한다.
  // 가속도 = 중력 + 회전 가속도(spinAccelX/Y, 상수) + 공기저항(-k·|v|·v). 반암시적 오일러 적분.
  // 반환: TIME_STEP_SEC 간격의 궤적 포인트, 마지막 포인트는 z=0 통과 지점.
  private static List<PitchPoint> flyBall(double releaseX, double vx, double vy, double vz,
                                          double spinAccelX, double spinAccelY) {
    List<PitchPoint> points = new ArrayList<>();
    double x = releaseX;
    double y = RELEASE_HEIGHT_CM;
    double z = MOUND_TO_PLATE_CM;
    points.add(new PitchPoint(0.0, x, y, z));
    int stepsPerSample = (int) Math.round(TIME_STEP_SEC / INTEGRATION_STEP_SEC);
    double dt = INTEGRATION_STEP_SEC;
    // 항력은 진행 방향 반대로만 작용해 vz 부호를 바꾸지 못하므로(vz < 0 유지) 반드시 z=0에 도달한다
    for (int step = 1; ; step++) {
      double dragPerSpeed = DRAG_CONSTANT_PER_CM * Math.sqrt(vx * vx + vy * vy + vz * vz);
      vx += (spinAccelX - dragPerSpeed * vx) * dt;
      vy += (spinAccelY - GRAVITY_CM_PER_SEC2 - dragPerSpeed * vy) * dt;
      vz += (-dragPerSpeed * vz) * dt;
      double prevX = x;
      double prevY = y;
      double prevZ = z;
      x += vx * dt;
      y += vy * dt;
      z += vz * dt;
      double t = step * dt;
      if (z <= 0) {
        // 이번 스텝 안에서 z=0을 지난 시점의 비율로 선형 보간해 정확한 도착 지점을 구한다
        double crossingFraction = prevZ / (prevZ - z);
        double crossingT = t - dt + crossingFraction * dt;
        double crossingX = prevX + (x - prevX) * crossingFraction;
        double crossingY = prevY + (y - prevY) * crossingFraction;
        points.add(new PitchPoint(crossingT, crossingX, crossingY, 0.0));
        return points;
      }
      if (step % stepsPerSample == 0) {
        points.add(new PitchPoint(t, x, y, z));
      }
    }
  }
}
