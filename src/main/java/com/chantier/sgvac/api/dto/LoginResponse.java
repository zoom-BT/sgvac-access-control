package com.chantier.sgvac.api.dto;

public record LoginResponse(
        String token,
        String username,
        String role
) {
}
