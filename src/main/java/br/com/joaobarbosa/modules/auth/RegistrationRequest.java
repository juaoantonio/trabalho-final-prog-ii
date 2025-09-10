package br.com.joaobarbosa.modules.auth;

import br.com.joaobarbosa.modules.users.UserRole;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record RegistrationRequest(
        @NotBlank
        String username,

        @NotBlank
        String password,

        @NotNull
        UserRole role
) {}
