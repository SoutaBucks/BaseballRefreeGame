package com.baseballrefree.pitch;

import com.baseballrefree.judgment.Judgment;
import com.baseballrefree.strikezone.StrikeZone;
import com.baseballrefree.strikezone.StrikeZoneCalculator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.convert.DataSizeUnit;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class PitchSimulatorTest {

  @Test
  @DisplayName("스트라이크 존에 스쳤을 때 판정을 똑바로 하는가?")
  void groundTruthIsStrikeWhenAnyPartOfBallTouchesZone() {
    StrikeZone zone = StrikeZoneCalculator.calculate(180.0);
    Random random = new Random(42);
    double ballRadiusCm = 3.65;

    for(int i = 0; i < 500; i++) {
      Pitch pitch = PitchSimulator.simulate(zone, random);
      boolean touches = zone.touches(pitch.plateX(), pitch.plateY(), ballRadiusCm);
      boolean swung = pitch.swingExtent() >= PitchSimulator.SWUNG_EXTENT_THRESHOLD;
      assertEquals(touches || swung ? Judgment.STRIKE : Judgment.BALL, pitch.groundTruth());
    }
  }

  @Test
  @DisplayName("투구할 때의 스트라이크 존은 변하면 안된다")
  void pitchCarriesTheZoneItWasThrownAgainst() {
    StrikeZone zone = StrikeZoneCalculator.calculate(170.0);
    Pitch pitch = PitchSimulator.simulate(zone, new Random(3));

    assertEquals(zone, pitch.zone());
  }

  @Test
  @DisplayName("궤적의 마지막 점은 홈플레이트 좌표(plateX, plateY)와 일치해야 한다")
  void trajectoryStartsAtReleaseAndEndsAtPlate() {
    StrikeZone zone = StrikeZoneCalculator.calculate(180);
    Pitch pitch = PitchSimulator.simulate(zone, new Random(5));

    assertFalse(pitch.trajectory().isEmpty());
    PitchPoint last = pitch.trajectory().getLast();
    assertEquals(pitch.plateX(), last.x(), 0.01);
    assertEquals(pitch.plateY(), last.y(), 0.01);
    assertEquals(0.0, last.z(), 0.01);
  }

  @Test
  @DisplayName("구속은 최소~최대 범위 내여야 한다")
  void speedIsWithinPitchTypeRange() {
    StrikeZone zone = StrikeZoneCalculator.calculate(180);
    Pitch pitch = PitchSimulator.simulate(zone, new Random(15));

    assertTrue(pitch.speedKmh() >= pitch.type().minSpeedKmh);
    assertTrue(pitch.speedKmh() <= pitch.type().maxSpeedKmh);
  }

  @Test
  @DisplayName("좌우로 존을 벗어나는 투구도 나와야 한다")
  void someSimulatedPitchesMissHorizontallyOutsideTheZone() {
    StrikeZone zone = StrikeZoneCalculator.calculate(175);
    Random random = new Random(87);
    boolean sawHorizontalMiss = false;
    
    // 한 번이라도 존에서 벗어나면 반복문 조건이 false가 되어 assertTrue로 빠져나가는 구조
    for(int i = 0; i < 500 && !sawHorizontalMiss; i++) {
      Pitch pitch = PitchSimulator.simulate(zone, random);
      boolean missLeft = pitch.plateX() < zone.leftX();
      boolean missRight = pitch.plateX() > zone.rightX();
      sawHorizontalMiss = missLeft || missRight;
    }
    assertTrue(sawHorizontalMiss);
  }
}