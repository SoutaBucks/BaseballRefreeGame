package com.baseballrefree.pitch;

/* 구종별 구속 범위와 무브먼트(포수 시점, 오른손 투수 기준)
*  horizontalBreakCm: +는 글러브 쪽(포수 기준 오른쪽), -는 팔 쪽(몸 쪽으로 휘어 들어옴)
* extraDropCm: 중력만 받는 공보다 더 떨어지는 양(+ 아래, -는 덜 떨어진다)
* breakLateness: 클수록 홈플레이트 가까이에서 꺾인다 (1이면 처음부터 일정한 느낌)
* */
public enum PitchType {
  FOUR_SEAM(145, 157, -20, -42, 2.0),        // IVB +15~18", HB -7~-9"
  TWO_SEAM(140, 152, -38, -23, 2.0),         // IVB +8~10",  HB -14~-16"
  CIRCLE_CHANGEUP(115, 130, -39, -14, 2.0),  // IVB +4~7",   HB -14~-17"
  CURVE(105, 122, 25, 30, 2.0),              // IVB -10~-14", HB +8~+12"
  SLIDER(125, 140, 16.5, 0, 2.5),            // IVB -2~+2",  HB +5~+8"
  FORKBALL(125, 138, -28, -9, 3.0);          // IVB +2~5",   HB -10~-12"

  public final double minSpeedKmh;
  public final double maxSpeedKmh;
  public final double horizontalBreakCm;
  public final double extraDropCm;
  public final double breakLateness;

  PitchType(double minSpeedKmh, double maxSpeedKmh, double horizontalBreakCm, double extraDropCm, double breakLateness) {
    this.minSpeedKmh = minSpeedKmh;
    this.maxSpeedKmh = maxSpeedKmh;
    this.horizontalBreakCm = horizontalBreakCm;
    this.extraDropCm = extraDropCm;
    this.breakLateness = breakLateness;
  }
}
