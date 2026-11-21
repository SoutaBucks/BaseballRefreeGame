package com.baseballrefree.api.dto;

import com.baseballrefree.game.HitOutcome;
import com.baseballrefree.judgment.Judgment;

import java.util.List;

public record PitchResponse(String pitchType, double speedKmh, List<PitchPointDto> trajectory,
                            double plateX, double plateY, Judgment groundTruth,
                            double zoneLeftX, double zoneRightX, double zoneBottomY, double zoneTopY,
                            double swingExtent, boolean inPlay, HitOutcome outcome,
                            int thrownOutAtBase, List<Integer> forceOutBases,
                            GameStateResponse state) {
}
