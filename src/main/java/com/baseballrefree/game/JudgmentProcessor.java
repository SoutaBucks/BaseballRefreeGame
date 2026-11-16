package com.baseballrefree.game;

import com.baseballrefree.judgment.Judgment;

public class JudgmentProcessor {
  private JudgmentProcessor() {}

  public static AtBatOutcome apply (GameState state, Judgment judgment) {
    if (judgment == Judgment.BALL) {
      state.getCount().addBall();
      if (state.getCount().getBalls() >= 4) {
        state.getCount().reset();
        return AtBatOutcome.WALK;
      }
      return AtBatOutcome.CONTINUE;
    }
    state.getCount().addStrike();
    if (state.getCount().getStrikes() >= 3) {
      state.getCount().reset();
      state.addOut();
      return AtBatOutcome.STRIKEOUT;
    }
    return AtBatOutcome.CONTINUE;
  }
}
