package com.baseballrefree.game;

import com.baseballrefree.pitch.Pitch;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

/*  thrownOutAtBase -> 안타 후 타자가 송구에 잡힌 베이스
    잡히지 않았으면 ThrowOutResolver.NOT_THROWN_OUT
    forceOutBases -> 땅볼 아웃에서 야수가 아웃을 잡은 베이스들 (송구 순서, 4=홈)
    땅볼이 아니면 빈 리스트
*/
@Getter
@RequiredArgsConstructor
public final class PitchResult {
  private final Pitch pitch;
  private final boolean inPlay;
  private final HitOutcome outcome;
  private final int thrownOutAtBase;
  private final List<Integer> forceOutBases;
  private final GameState state;
}
