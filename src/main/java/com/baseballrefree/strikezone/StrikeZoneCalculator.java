package com.baseballrefree.strikezone;

public final class StrikeZoneCalculator {
  private static final double PLATE_WIDTH_CM = 43.18; // 홈플레이트의 실제 가로 너비
  private static final double MARGIN_CM = 2.0;        // 홈플레이트 경계선 바깥쪽으로 인정해주는 보정 여유 공간
  private static final double HALF_WIDTH_CM = PLATE_WIDTH_CM / 2 + MARGIN_CM;  // 홈플레이트 정중앙을 기준으로 양 옆으로 인정해주는 거리
  private static final double TOP_RATIO = 0.564;      // 타자 신장에 곱해지는 스트라이크 존 상단 경계선 비율
  private static final double BOTTOM_RATIO = 0.276;   // 타자 신장에 곱해지는 스트라이크 존 하단 경계선 비율

  private StrikeZoneCalculator() {}

  public static StrikeZone calculate(double batterHeightCm) {
    double bottomY = batterHeightCm * BOTTOM_RATIO;
    double topY = batterHeightCm * TOP_RATIO;
    return new StrikeZone(-HALF_WIDTH_CM, HALF_WIDTH_CM, bottomY, topY);
  }
}