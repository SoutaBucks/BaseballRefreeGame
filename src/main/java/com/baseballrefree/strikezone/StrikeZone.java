package com.baseballrefree.strikezone;

public record StrikeZone(double leftX, double rightX, double bottomY, double topY) {
  // 스트라이크 존에 공이 포함이 되었냐?
  public boolean contains(double x, double y) {
    return x >= leftX && x <= rightX && y >= bottomY && y <= topY;
  }

  private double getDistance(double x, double y) {
    return Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
  }

  // 공이 스트라이크 존에 일부라도 걸치면 스트라이크이므로 true를 반환
  public boolean touches(double ballCenterX, double ballCenterY, double ballRadiusCm) {
    boolean overlapsHorizontally = ballCenterX >= (leftX - ballRadiusCm) && ballCenterY <= (rightX + ballRadiusCm);
    boolean overlapsVertically = ballCenterY >= (bottomY - ballRadiusCm) && ballCenterX <= (leftX + ballRadiusCm);
    boolean overlapsCorner =
            (getDistance(ballCenterX - rightX, bottomY - ballCenterY) <= ballRadiusCm) || // 4사분면
            (getDistance(leftX - ballCenterX, bottomY - ballCenterY) <= ballRadiusCm)  || // 3사분면
            (getDistance(leftX - ballCenterX, ballCenterY - topY) <= ballRadiusCm) ||     // 2사분면
            (getDistance(ballCenterX - rightX, ballCenterY - topY) <= ballRadiusCm);      // 1사분면
    return overlapsHorizontally && overlapsVertically && overlapsCorner;
  }
}