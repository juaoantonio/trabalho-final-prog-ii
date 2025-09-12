package br.com.joaobarbosa.modules.seat.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import java.util.UUID;

public record CreateSeatRequest(
        @NotNull(message = "O ID da sala não pode ser nulo") UUID roomId,
        @NotBlank(message = "O rótulo da linha não pode estar em branco") String rowLabel,
        @NotNull @Min(value = 1, message = "O número da coluna deve ser pelo menos 1") Integer colNumber,
        @NotBlank(message = "O rótulo do assento não pode estar em branco") String label) {}