package com.baseballrefree.game;

import lombok.Getter;
import lombok.Setter;

@Getter
public final class Bases {
  private boolean first;
  private boolean second;
  private boolean third;

  // 루상의 주자를 한 번에 정한다 (안타로 주자가 이동한 결과를 반영할 때 사용)
  public void setRunners(boolean first, boolean second, boolean third) {
    this.first = first;
    this.second = second;
    this.third = third;
  }

  public void clear() {
    first = false;
    second = false;
    third = false;
  }

  public int runnerCount() {
    return (first ? 1 : 0) + (second ? 1 : 0) + (third ? 1 : 0);
  }

  public void removeRunnerAt(int base) {
    switch (base) {
      case 1 -> first = false;
      case 2 -> second = false;
      case 3 -> third = false;
      default -> throw new IllegalArgumentException("Unexpected base: " + base);
    }
  }

  // 타자가 1루로 가면서 다른 주자가 한 베이스씩 진루한다. 반환값 -> 득점 수
  public int forceAdvance() {
    boolean basesLoaded = first && second && third;
    int runs = basesLoaded ? 1 : 0;
    boolean pushedToThird = first && second;
    boolean secondOccupied = second || first;
    setRunners(true, secondOccupied, third || pushedToThird);
    return runs;
  }
}
