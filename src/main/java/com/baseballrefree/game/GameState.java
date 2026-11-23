package com.baseballrefree.game;

import com.baseballrefree.player.Batter;
import com.baseballrefree.player.Pitcher;
import lombok.Getter;
import lombok.Setter;

@Getter
public class GameState {
  private static final int TOTAL_INNINGS = 9;

  // 불변식이 없는 단순 교체 값만 @Setter
  @Setter private Batter currentBatter;
  @Setter private Pitcher currentPitcher;

  // 규칙이 있는 값: 변경은 addXXX()/useVar() 등으로만 가능 (setter X)
  private int inning = 1;
  private boolean topOfInning = true;
  private boolean gameOver = false;
  private int homeScore = 0;
  private int awayScore = 0;
  private int outs = 0;
  private int varRemaining = 3;
  private final Count count = new Count();
  private final Bases bases = new Bases();

  // 배열은 Lombok 기본 getter가 원본을 노출하므로 clone() 반환 getter를 직접 작성
  private final int[] awayInningRuns = new int[TOTAL_INNINGS];
  private final int[] homeInningRuns = new int[TOTAL_INNINGS];

  public void addHomeScore(int runs) {
    homeScore += runs;
    homeInningRuns[inning - 1] += runs;
  }
  public int[] getHomeInningRuns() { return homeInningRuns.clone(); }

  public void addAwayScore(int runs) {
    awayScore += runs;
    awayInningRuns[inning - 1] += runs;
  }
  public int[] getAwayInningRuns() { return awayInningRuns.clone(); }

  public void addOut() { outs++; }
  public void resetOuts() { outs = 0; }
  public void startNextInning() {
    inning++;
    topOfInning = true;
  }
  public void startBottomOfInning() { topOfInning = false; }
  public void endGame() { gameOver = true; }
  public void useVar() {
    if (varRemaining > 0)
      varRemaining--;
  }
}
