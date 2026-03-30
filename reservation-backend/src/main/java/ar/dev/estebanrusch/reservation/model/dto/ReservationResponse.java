package ar.dev.estebanrusch.reservation.model.dto;

import java.time.LocalDate;
import java.time.LocalTime;

import ar.dev.estebanrusch.reservation.model.entity.Reservation;
import ar.dev.estebanrusch.reservation.model.entity.ReservationStatus;

public record ReservationResponse(
        Long id,
        String customerName,
        LocalDate date,
        LocalTime time,
        String service,
        ReservationStatus status) {

    public static ReservationResponse from(Reservation reservation) {
        return new ReservationResponse(
                reservation.getId(),
                reservation.getCustomerName(),
                reservation.getDate(),
                reservation.getTime(),
                reservation.getService(),
                reservation.getStatus());
    }
}
