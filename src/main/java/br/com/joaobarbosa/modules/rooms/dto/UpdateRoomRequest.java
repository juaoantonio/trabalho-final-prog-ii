package br.com.joaobarbosa.modules.rooms.dto;

import jakarta.validation.constraints.Min;

public record UpdateRoomRequest(String name, @Min(1) Integer rows, @Min(1) Integer cols) {}
