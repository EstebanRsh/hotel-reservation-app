package ar.dev.estebanrusch.reservation.model.dto;

import java.time.LocalDate;
import java.time.LocalTime;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateReservationRequest(
        @NotBlank @Size(max = 100) String customerName,
        @NotNull LocalDate date,
        @NotNull LocalTime time,
        @NotBlank @Size(max = 100) String service) {
}
