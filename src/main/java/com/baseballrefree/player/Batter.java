package com.baseballrefree.player;

/* name -> 타자 이름
*  heightCm -> 타자 키
*  battingAverage -> 타율
*  homeToFirstSec -> 타자 주력 속도...?
*/

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public final class Batter {
  private final String name;
  private final double heightCm;
  private final double battingAverage;
  private final double homeToFirstSec;
}