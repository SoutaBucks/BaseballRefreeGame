package com.baseballrefree.api.dto;

import com.baseballrefree.judgment.Judgment;
import jakarta.validation.constraints.NotNull;

public record JudgmentRequest(@NotNull Judgment judgment) {
}
