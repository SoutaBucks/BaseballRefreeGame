package com.baseballrefree.game;

import lombok.Getter;

@Getter
public final class Count {
  private int balls;
  private int strikes;

  public void addBall() { balls++; }
  public void addStrike() { strikes++; }
  public void reset() {
    balls = 0;
    strikes = 0;
  }
}