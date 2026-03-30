package ar.dev.estebanrusch.reservation.model.dto;

public record LoginResponse(String token, String username, String role) {
}
