package br.com.joaobarbosa.modules.rooms.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateRoomRequest(
    @NotBlank(message = "O nome da sala não pode estar em branco") String name,
    @NotNull @Min(value = 1, message = "O número de linhas deve ser pelo menos 1") Integer rows,
    @NotNull @Min(value = 1, message = "O número de colunas deve ser pelo menos 1") Integer cols) {}
