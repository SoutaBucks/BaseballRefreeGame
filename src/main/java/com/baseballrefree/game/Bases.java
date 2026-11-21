package com.baseballrefree.game;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public final class Bases {
  private boolean first;
  private boolean second;
  private boolean third;

  public void clear() {
    first = false;
    second = false;
    third = false;
  }

  public int runnerCount() {
    return (first ? 1 : 0) + (second ? 1 : 0) + (third ? 1 : 0);
  }
}
