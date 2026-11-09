package com.baseballrefree.game;

import java.util.Random;

/* FOUL_SHARE_OF_NON_HIT -> 안타가 아닐 때 (1 - 타율) 중 파울 비중
* GROUND_OUT_SHARE_OF_OUT -> 아웃 중 땅볼 비중 (나머지는 뜬공 / 라이너)
* SINGLE_SHARE, DOUBLE_SHARE, TRIPLE_SHARE -> 안타 중 각루타의 비율
* */
public class HitOutcomeResolver {
  private static final double FOUL_SHARE_OF_NON_HIT = 0.30;
  private static final double GROUND_OUT_SHARE_OF_OUT = 0.45;
  private static final double SINGLE_SHARE = 0.7;
  private static final double DOUBLE_SHARE = 0.2;
  private static final double TRIPLE_SHARE = 0.03;

  private HitOutcomeResolver() {}

  public static HitOutcome resolve(double battingAverage, Random random) {
    double roll = random.nextDouble();
    if (roll < battingAverage) {
      double hitRoll = random.nextDouble();
      if (hitRoll < SINGLE_SHARE)
        return HitOutcome.SINGLE;
      else if (hitRoll < SINGLE_SHARE + DOUBLE_SHARE)
        return HitOutcome.DOUBLE;
      else if (hitRoll < SINGLE_SHARE + DOUBLE_SHARE + TRIPLE_SHARE)
        return HitOutcome.TRIPLE;
      return HitOutcome.HOMERUN;
    }
    double nonHitRoll = random.nextDouble();
    if (nonHitRoll < FOUL_SHARE_OF_NON_HIT)
      return HitOutcome.FOUL;
    return (random.nextDouble() < GROUND_OUT_SHARE_OF_OUT ? HitOutcome.GROUND_OUT : HitOutcome.OUT);
  }
}
