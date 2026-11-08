package com.baseballrefree.player;

/* name -> 타자 이름
*  heightCm -> 타자 키
*  battingAverage -> 타율
*  homeToFirstSec -> 타자 주력 속도...?
*/
public record Batter(String name, double heightCm, double battingAverage, double homeToFirstSec) {
}
