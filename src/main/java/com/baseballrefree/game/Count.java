package com.baseballrefree.game;

public final class Count {
  private int balls;
  private int strikes;

  public int balls() {  return balls; }
  public int strikes() { return strikes; }
  public void addBall() { balls++; }
  public void addStrike() { strikes++; }
  public void reset() {
    balls = 0;
    strikes = 0;
  }
}
