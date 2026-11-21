package com.baseballrefree.pitch;

import com.baseballrefree.judgment.Judgment;
import com.baseballrefree.strikezone.StrikeZone;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

/*
* zone: 이 투구를 할 대의 타자 기준 스트라이크 존, 타자가 바뀐 뒤에도 해당 투구의 판정 근거를 보여주기 위해 함께 보관
* swingExtent: 체크스윙 시 배트가 돈 정도 (0~1), 0이면 스윙 시도 없음
* */
@Getter
@RequiredArgsConstructor
public final class Pitch {
  private final PitchType type;
  private final double speedKmh;
  private final List<PitchPoint> trajectory;
  private final double plateX;
  private final double plateY;
  private final Judgment groundTruth;
  private final StrikeZone zone;
  private final double swingExtent;

  public boolean isCheckSwing() {
    return swingExtent > 0;
  }
}
