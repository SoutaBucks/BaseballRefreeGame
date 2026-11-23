package com.baseballrefree.game;

import com.baseballrefree.judgment.Judgment;
import com.baseballrefree.pitch.Pitch;
import com.baseballrefree.pitch.PitchSimulator;
import com.baseballrefree.player.PlayerGenerator;
import com.baseballrefree.strikezone.StrikeZone;
import com.baseballrefree.strikezone.StrikeZoneCalculator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;

/*
* 게임 한 판의 진행을 조율하는 서비스
* 규칙 계산은 협력 컴포넌트에 위임한다
*/
@Service
@RequiredArgsConstructor
public class GameService {
  // 투구 중 실제로 타자가 쳐서 인프레이가 되는 비율
  private static final double IN_PLAY_PROBABILITY = 0.25;

  // Random과 규칙 게산 Component들 (생성자 주입)
  private final Random random;
  private final PitchSimulator pitchSimulator;
  private final StrikeZoneCalculator strikeZoneCalculator;
  private final PlayerGenerator playerGenerator;
  private final HitOutcomeResolver hitOutcomeResolver;
  private final GroundBallResolver groundBallResolver;
  private final BaseAdvancer baseAdvancer;
  private final JudgmentProcessor judgmentProcessor;
  private final InningManager inningManager;
  private final ThrowOutResolver throwOutResolver;

  // 진행 중인 게임 상태. startGame() 전에는 null
  private GameState state;

  // 새 게임을 만들고 첫 타자/투수를 정한다
  public GameState startGame() {
    state = new GameState();
    state.setCurrentBatter(playerGenerator.generateBatter(random));
    state.setCurrentPitcher(playerGenerator.generatePitcher(random));
    return state;
  }

  // 공 한 개를 던지고, 인플레이면 타구 결과까지 경기 상태에 반영한다
  public PitchResult throwPitch() {
    requireGameInProgress();
    double batterHeightCm = state.getCurrentBatter().heightCm();
    StrikeZone zone = strikeZoneCalculator.calculate(batterHeightCm);
    Pitch pitch = pitchSimulator.simulate(zone, random);

    double inPlayRoll = random.nextDouble();
    // 체크스윙은 공을 맞히지 못했으므로 인플레이가 될 수 없다
    boolean inPlay = !pitch.isCheckSwing() && inPlayRoll < IN_PLAY_PROBABILITY;
    if (!inPlay)
      return new PitchResult(pitch, false, null, ThrowOutResolver.NOT_THROWN_OUT, List.of(), state);

    double battingAverage = state.getCurrentBatter().battingAverage();
    double batterHomeToFirstSec = state.getCurrentBatter().homeToFirstSec();
    HitOutcome outcome = hitOutcomeResolver.resolve(battingAverage, random);
    int thrownOutAtBase = throwOutResolver.resolve(outcome, batterHomeToFirstSec, random);

    // 야수의 송구 판단은 타구 직전 주자/아웃 상황을 보고 내린다
    List<Integer> forceOutBases = outcome == HitOutcome.GROUND_OUT ?
        groundBallResolver.resolve(state.getBases(), state.getOuts(), batterHomeToFirstSec, random) : List.of();
    applyHitOutCome(outcome, thrownOutAtBase, forceOutBases);
    return new PitchResult(pitch, true, outcome, thrownOutAtBase, forceOutBases, state);
  }

  // 심판 판정을 반영하고, 삼진/볼넷이면 후속 처리(진루, 이닝 교체, 타자 교체)를 한다.
  public GameState submitJudgment(Judgment judgment) {
    requireGameInProgress();
    AtBatOutcome outcome = judgmentProcessor.apply(state, judgment);
    if (outcome == AtBatOutcome.STRIKEOUT) {
      inningManager.afterOutsUpdated(state);
      advanceBatterIfInningContinues();
    } else if (outcome == AtBatOutcome.WALK) {
      int runs = state.getBases().forceAdvance();
      addRuns(runs);
      advanceBatterIfInningContinues();
    }
    return state;
  }

  // 현재 경기 상태를 반환한다 (게임 시작 전이면 예외)
  public GameState getState() {
    requireGameStarted();
    return state;
  }

  public GameState useVar() {
    requireGameInProgress();
    state.useVar();
    return state;
  }

  // 타구 결과를 주자/아웃/득점/카운트에 반영한다
  private void applyHitOutCome(HitOutcome outcome, int thrownOutAtBase, List<Integer> forceOutBases) {
    if (outcome == HitOutcome.FOUL) {
      if (state.getCount().getStrikes() < 2)
        state.getCount().addStrike();
      return;
    }
    int runs = outcome == HitOutcome.GROUND_OUT
        ? applyGroundOut(forceOutBases) : baseAdvancer.apply(state, outcome);
    if (thrownOutAtBase != ThrowOutResolver.NOT_THROWN_OUT) {
      // 주자들은 안타대로 진루하고, 목표 베이스에 도착하려던 타자만 아웃된다.
      state.getBases().removeRunnerAt(thrownOutAtBase);
      state.addOut();
      // 타자가 1루에서 잡혀 3아웃이 되면 해당 플레이의 득점은 취소된다
      boolean inningEndingForceOut = thrownOutAtBase == 1 && inningManager.isHalfInningOver(state);
      if (inningEndingForceOut) runs = 0;
    }
    addRuns(runs);
    state.getCount().reset();
    inningManager.afterOutsUpdated(state);
    advanceBatterIfInningContinues();
  }

  // 땅볼: 타자와 포스된 주자들이 한 베이스씩 밀려 나가고, 야수가 잡은 베이스의 주자가 아웃된다. 반환 -> 인정된 득점
  private int applyGroundOut(List<Integer> forceOutBases) {
    int runs = state.getBases().forceAdvance();
    for (int base : forceOutBases) {
      if (base == GroundBallResolver.HOME_PLATE) runs = 0;
      else state.getBases().removeRunnerAt(base);
      state.addOut();
    }
    // 포스 아웃으로 이닝이 끝나면 그 플레이의 득점은 인정되지 않는다
    boolean inningEndingForceOut = inningManager.isHalfInningOver(state);
    return inningEndingForceOut ? 0 : runs;
  }

  // 공격 중인 팀(초 = 원정, 말 = 홈)에 득점을 더한다
  private void addRuns(int runs) {
    if (runs <= 0)  return;
    if (state.isTopOfInning()) state.addAwayScore(runs);
    else state.addHomeScore(runs);
  }

  // 경기가 끝나지 않았으면 다음 타자로 교체한다
  private void advanceBatterIfInningContinues() {
    if (!state.isGameOver())
      state.setCurrentBatter(playerGenerator.generateBatter(random));
  }

  // 게임이 시작되지 않았으면 예외를 던진다
  private void requireGameStarted() {
    if (state == null)
      throw new IllegalStateException("Game has not been started");
  }

  // 게임이 시작됐고 아직 끝나지 않았어야 한다
  private void requireGameInProgress() {
    requireGameStarted();
    if (state.isGameOver())
      throw new IllegalStateException("Game has already over");
  }
}
