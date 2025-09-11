package br.com.joaobarbosa.modules.room.dto;

import jakarta.validation.constraints.Min;

public record UpdateRoomRequest(String name, @Min(1) Integer rows, @Min(1) Integer cols) {}
