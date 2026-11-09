package com.baseballrefree.player;

import com.baseballrefree.game.ThrowOutResolver;

import java.util.List;
import java.util.Random;

public final class PlayerGenerator {
  private static final List<String> NAMES = List.of(
      "김민준", "이서준", "박도윤", "최지호", "정우진",
      "강현우", "조은우", "윤재윤", "장시우", "임유준"
  );

  private PlayerGenerator() {}

  public static Batter generateBatter(Random random) {
    String name = NAMES.get(random.nextInt(NAMES.size()));
    double heightCm = 160.0 + random.nextDouble() * 25.0;
    double battingAverage = 0.200 + random.nextDouble() * 0.110;
    double speedRangeSec = ThrowOutResolver.SLOWEST_HOME_TO_FIRST_SEC - ThrowOutResolver.FASTEST_HOME_TO_FIRST_SEC;
    double homeToFirstSec = ThrowOutResolver.FASTEST_HOME_TO_FIRST_SEC + random.nextDouble() * speedRangeSec;
    return new Batter(name, heightCm, battingAverage, homeToFirstSec);
  }

  public static Pitcher generatePitcher(Random random) {
    String name = NAMES.get(random.nextInt(NAMES.size()));
    return new Pitcher(name);
  }
}
