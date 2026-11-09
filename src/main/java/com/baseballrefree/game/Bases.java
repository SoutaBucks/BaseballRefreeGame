package com.baseballrefree.game;

public final class Bases {
  private boolean first;
  private boolean second;
  private boolean third;

  public boolean first() { return first; }
  public boolean second() { return second; }
  public boolean third() { return third; }

  public void setFirst(boolean v) { first = v; }
  public void setSecond(boolean v) { second = v; }
  public void setThird(boolean v) { third = v; }

  public void clear() {
    first = false;
    second = false;
    third = false;
  }

  public int runnerCount() {
    return (first ? 1 : 0) + (second ? 1 : 0) + (third ? 1 : 0);
  }
}
