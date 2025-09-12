package br.com.joaobarbosa.modules.seat.dto;

import jakarta.validation.constraints.Min;
import java.util.UUID;

public record UpdateSeatRequest(
        UUID roomId,
        String rowLabel,
        @Min(value = 1) Integer colNumber,
        String label) {}