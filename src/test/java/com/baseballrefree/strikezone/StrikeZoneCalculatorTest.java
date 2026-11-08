package com.baseballrefree.strikezone;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

class StrikeZoneCalculatorTest {

  @Test
  @DisplayName("좌우 스트라이크 존이 실제와 일치하는가?")
  void widthIsPlateWidthPlusTwoCmMargin() {
    StrikeZone zone = StrikeZoneCalculator.calculate(180.0);
    assertEquals(-23.59, zone.leftX(), 0.001);
    assertEquals(23.59, zone.rightX(), 0.001);
  }

  @Test
  @DisplayName("위 아래 스트라이크 존이 타자의 신장에 비례하여 적용되는가?")
  void heightScalesWithBatterHeight() {
    StrikeZone zone = StrikeZoneCalculator.calculate(170.0);
    assertEquals(170 * 0.564, zone.topY(), 0.001);
    assertEquals(170 * 0.276, zone.bottomY(), 0.001);
  }
}