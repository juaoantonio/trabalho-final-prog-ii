package br.com.joaobarbosa.modules.auth;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank(message = "`username` não pode estar vazio")
        String username,

        @NotBlank(message = "`password` não pode estar vazio")
        String password
) {}
