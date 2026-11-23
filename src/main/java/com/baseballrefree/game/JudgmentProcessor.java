package com.baseballrefree.game;

import com.baseballrefree.judgment.Judgment;
import org.springframework.stereotype.Component;

@Component
public class JudgmentProcessor {
  private static final int BALLS_FOR_WALK = 4;
  private static final int STRIKES_FOR_STRIKEOUT = 3;

  public AtBatOutcome apply (GameState state, Judgment judgment) {
    Count count = state.getCount();
    if (judgment == Judgment.BALL) {
      count.addBall();
      if (count.getBalls() < BALLS_FOR_WALK)
        return AtBatOutcome.CONTINUE;
      count.reset();
      return AtBatOutcome.WALK;
    }
    count.addStrike();
    if (count.getStrikes() < STRIKES_FOR_STRIKEOUT)
      return AtBatOutcome.CONTINUE;
    count.reset();
    state.addOut();
    return AtBatOutcome.STRIKEOUT;
  }
}
