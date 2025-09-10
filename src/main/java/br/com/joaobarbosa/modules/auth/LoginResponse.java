package br.com.joaobarbosa.modules.auth;

import jakarta.validation.constraints.NotBlank;

public record LoginResponse(
        String token
) {}
