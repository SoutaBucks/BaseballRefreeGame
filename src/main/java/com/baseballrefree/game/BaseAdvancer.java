package com.baseballrefree.game;

public final class BaseAdvancer {
  private BaseAdvancer() {}

  // 타구 결과(outcome)에 따라 주자를 진루시키고 아웃을 기록한다.
  // state -> 현재 경기 상태, 반환값 -> 이번 타구로 들어온 득점
  public static int apply(GameState state, HitOutcome outcome) {
    Bases bases = state.getBases();
    return switch (outcome) {
      case FOUL -> 0;
      case OUT -> {
        state.addOut();
        yield 0;
      }
      case SINGLE -> {
        int runs = bases.isThird() ? 1 : 0;
        boolean toThird = bases.isSecond();
        boolean toSecond = bases.isFirst();
        bases.setThird(toThird);
        bases.setSecond(toSecond);
        bases.setFirst(true);
        yield runs;
      }
      case DOUBLE -> {
        int runs = (bases.isThird() ? 1 : 0) + (bases.isSecond() ? 1 : 0);
        boolean toThird = bases.isSecond();
        bases.setFirst(toThird);
        bases.setSecond(true);
        bases.setFirst(false);
        yield runs;
      }
      case TRIPLE -> {
        int runs = (bases.isThird() ? 1 : 0) + (bases.isSecond() ? 1 : 0) + (bases.isFirst() ? 1 : 0);
        bases.clear();
        bases.setThird(true);
        yield runs;
      }
      case HOMERUN -> {
        int runs = bases.runnerCount() + 1;
        bases.clear();
        yield runs;
      }
      case GROUND_OUT -> throw new IllegalStateException("GROUND_OUT must be resolved by GroundBallResolver");
    };
  }
}
