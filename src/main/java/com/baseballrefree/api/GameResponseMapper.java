package com.baseballrefree.api;

import com.baseballrefree.api.dto.GameStateResponse;
import com.baseballrefree.api.dto.PitchPointDto;
import com.baseballrefree.api.dto.PitchResponse;
import com.baseballrefree.game.GameState;
import com.baseballrefree.game.PitchResult;
import com.baseballrefree.pitch.Pitch;
import com.baseballrefree.player.Batter;
import com.baseballrefree.strikezone.StrikeZone;
import com.baseballrefree.strikezone.StrikeZoneCalculator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class GameResponseMapper {

  // 현재 타자 신장으로 존 경계를 계산하는 협력 컴포넌트
  private final StrikeZoneCalculator strikeZoneCalculator;

  // state -> 경기 상태, 반환값 -> 존 경계까지 포함한 상태 응답
  public GameStateResponse toStateResponse(GameState state) {
    Batter batter = state.getCurrentBatter();
    StrikeZone zone = strikeZoneCalculator.calculate(batter.heightCm());
    return new GameStateResponse(
        state.getInning(), state.isTopOfInning(), state.getHomeScore(), state.getAwayScore(),
        state.getOuts(), state.getCount().getBalls(), state.getCount().getStrikes(),
        state.getBases().isFirst(), state.getBases().isSecond(), state.getBases().isThird(),
        batter.name(), batter.heightCm(), batter.battingAverage(), batter.homeToFirstSec(),
        state.getCurrentPitcher().name(), state.getVarRemaining(), state.isGameOver(),
        zone.leftX(), zone.rightX(), zone.bottomY(), zone.topY(),
        state.getAwayInningRuns(), state.getHomeInningRuns()
    );
  }

  // result -> 투구 결과, 반환값 -> 궤적, 타구 결과, 투구 직후 상태를 담은 응답
  public PitchResponse toPitchResponse(PitchResult result) {
    Pitch pitch = result.pitch();
    StrikeZone zone = pitch.zone();
    List<PitchPointDto> trajectory =  pitch.trajectory().stream()
        .map(p -> new PitchPointDto(p.t(), p.x(), p.y(), p.z()))
        .toList();
    GameStateResponse stateResponse = toStateResponse(result.state());
    return new PitchResponse(pitch.type().name(), pitch.speedKmh(), trajectory, pitch.plateX(), pitch.plateY(),
        pitch.groundTruth(), zone.leftX(), zone.rightX(), zone.bottomY(), zone.topY(), pitch.swingExtent(),
        result.inPlay(), result.outcome(), result.thrownOutAtBase(), result.forceOutBases(), stateResponse);
  }
}
