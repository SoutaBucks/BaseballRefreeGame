package com.baseballrefree.game;

import java.util.Random;

// 안타를 쳐도 발이 느린 타자는 수비의 송구에 잡힐 수 있다
public final class ThrowOutResolver {
  public static final int NOT_THROWN_OUT = 0;
  public static final double FASTEST_HOME_TO_FIRST_SEC = 3.9;
  public static final double SLOWEST_HOME_TO_FIRST_SEC = 4.8;

  // 가장 느린 타자가 각 안타에서 잡힐 확률
  // 빠를수록 선형으로 줄어 가장 빠른 타자는 0이다
  private static final double SINGLE_MAX_THROW_OUT_CHANCE = 0.35;
  private static final double DOUBLE_MAX_THROW_OUT_CHANCE = 0.30;
  private static final double TRIPLE_MAX_THROW_OUT_CHANCE = 0.40;

  // 가장 빠른 타자가 가장 느린 타자 확률의 몇 배로 잡히는지(0.15 = 15%), 아무리 빨리도 잡힌다
  private static final double FASTEST_RUNNER_CHANCE_SHARE = 0.15;

  private ThrowOutResolver() {}

  //반환값: 타자가 잡힌 베이스(1~3), 잡히지 않았으면 NOT_THROWN_OUT
  public static int resolve(HitOutcome outcome, double homeToFirstSec, Random random) {
    int targetBase = targetBaseFor(outcome);
    if (targetBase == NOT_THROWN_OUT) {
      return NOT_THROWN_OUT;
    }

    double throwOutChance = maxThrowOutChanceFor(outcome) * throwOutChanceShareOf(homeToFirstSec);
    boolean thrownOut = random.nextDouble() < throwOutChance;
    return thrownOut ? targetBase : NOT_THROWN_OUT;
  }

  // 0(가장 빠름) ~ 1(가장 느림)
  static double slownessOf(double homeToFirstSec) {
    double speedRangeSec = SLOWEST_HOME_TO_FIRST_SEC - FASTEST_HOME_TO_FIRST_SEC;
    double slowness = (homeToFirstSec - FASTEST_HOME_TO_FIRST_SEC) / speedRangeSec;
    return Math.max(0.0, Math.min(1.0, slowness));
  }

  private static int targetBaseFor(HitOutcome outcome) {
    return switch (outcome) {
      case SINGLE -> 1;
      case DOUBLE -> 2;
      case TRIPLE -> 3;
      default -> NOT_THROWN_OUT;
    };
  }

  // 최대 확률 중 실제로 적용할 비율: FASTEST_RUNNER_CHANCE_SHARE 0.0~1.0
  private static double throwOutChanceShareOf(double homeToFirstSec) {
    double shareRange = 1.0 - FASTEST_RUNNER_CHANCE_SHARE;
    return FASTEST_RUNNER_CHANCE_SHARE + shareRange * slownessOf(homeToFirstSec);
  }

  private static double maxThrowOutChanceFor(HitOutcome outcome) {
    return switch (outcome) {
      case SINGLE -> SINGLE_MAX_THROW_OUT_CHANCE;
      case DOUBLE -> DOUBLE_MAX_THROW_OUT_CHANCE;
      case TRIPLE -> TRIPLE_MAX_THROW_OUT_CHANCE;
      default -> 0.0;
    };
  }
}
