package com.baseballrefree.game;

import java.util.List;
import java.util.Random;

/* 땅볼 아웃에서 야수가 어느 베이스로 던질지 판단한다
*  포스 상황이 아니거나 2아웃이면 가장 확실한 1루에서 타자를 잡는다
*  0, 1아웃 포스 상황이면 선행 주자를 먼저 잡고 1루로 병살 시도를 한다
*  병살 성공률은 타자가 느릴 수록 좋다
* */
public final class GroundBallResolver {
  public static final int NO_FORCE = 0;
  public static final int HOME_PLATE = 4;
  private static final int FIRST_BASE = 1;
  private static final int OUTS_BEFORE_INNING_ENDS = 2;
  private static final double FASTEST_BATTER_DOUBLE_PLAY_CHANCE = 0.25;
  private static final double SLOWEST_BATTER_DOUBLE_PLAY_CHANCE = 0.85;

  private GroundBallResolver() {}

  // 반환값 : 아웃을 잡은 베이스들 (송구 순서), 4는 홈, 선행 주자만 잡고 1루가 빠졌으면 병살 실패
  public static List<Integer> resolve(Bases bases, int outs, double batterHomeToFirstSec, Random random) {
    int leadForceBase = leadForcedBase(bases);
    boolean takeSureOutAtFirst = leadForceBase == NO_FORCE || outs >= OUTS_BEFORE_INNING_ENDS;
    if (takeSureOutAtFirst)
      return List.of(FIRST_BASE);
    double doublePlayChance = doublePlayChanceFor(batterHomeToFirstSec);
    boolean turnedDoublePlay = random.nextDouble() < doublePlayChance;
    return turnedDoublePlay ? List.of(leadForceBase, FIRST_BASE) : List.of(leadForceBase);
  }

  // 1루부터 연속으로 찬 주자들은 모두 밀려나므로, 그중 가장 앞 주자가 향하는 베이스가 선행 포스 베이스다.
  static int leadForcedBase(Bases bases) {
    if (!bases.first())
      return NO_FORCE;
    else if(!bases.second())
      return 2;
    else if(!bases.third())
      return 3;
    return HOME_PLATE;
  }

  private static double doublePlayChanceFor(double batterHomeToFirstSec) {
    double slowness = ThrowOutResolver.slownessOf(batterHomeToFirstSec);
    double changeRange = SLOWEST_BATTER_DOUBLE_PLAY_CHANCE - FASTEST_BATTER_DOUBLE_PLAY_CHANCE;
    return FASTEST_BATTER_DOUBLE_PLAY_CHANCE + changeRange * slowness;
  }
}
