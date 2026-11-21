package com.baseballrefree.game;

public final class InningManager {
  private static final int OUTS_PER_HALF_INNING = 3;
  private static final int TOTAL_INNINGS = 9;

  private InningManager() {}

  public static void afterOutsUpdated(GameState state) {
    if (state.getOuts() < OUTS_PER_HALF_INNING) return;
    state.resetOuts();
    state.getBases().clear();
    state.getCount().reset();

    if (!state.isTopOfInning()) {
      if (state.getInning() >= TOTAL_INNINGS) {
        state.setGameOver(true);
        return;
      }
      state.setInning(state.getInning() + 1);
      state.setTopOfInning(true);
    } else state.setTopOfInning(false);
  }
}
