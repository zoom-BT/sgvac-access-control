package com.chantier.sgvac.api.dto;

import jakarta.validation.constraints.NotBlank;

public record AccessEvaluateRequest(
        @NotBlank String badgeCode,
        String checkpoint
) {
}
