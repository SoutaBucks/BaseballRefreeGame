package com.baseballrefree.api.dto;

public record GameStateResponse(int inning, boolean topOfInning, int homeScore, int awayScore,
                                int outs, int balls, int strikes,
                                boolean first, boolean second, boolean third,
                                String batterName, double batterHeightCm, double batterBattingAverage,
                                double batterHomeToFirstSec,
                                String pitcherName, int varRemaining, boolean gameOver,
                                double zoneLeftX, double zoneRightX, double zoneBottomY, double zoneTopY,
                                int[] awayInningRuns, int[] homeInningRuns) {

}
