package com.baseballrefree.game;

public final class InningManager {
  private static final int OUTS_PER_HALF_INNING = 3;
  private static final int TOTAL_INNINGS = 9;

  private InningManager() {}

  // state -> 아웃카운트가 갱신된 경기 상태. 3아웃이면 아무것도 하지 않음
  public static void afterOutsUpdated(GameState state) {
    if (state.getOuts() < OUTS_PER_HALF_INNING) return;
    state.resetOuts();
    state.getBases().clear();
    state.getCount().reset();

    if (!state.isTopOfInning())
      state.startBottomOfInning();
    else if (state.getInning() >= TOTAL_INNINGS)
      state.endGame();
    else
      state.startNextInning();
  }
}
