package ar.dev.estebanrusch.reservation.repository;

import java.time.LocalDate;
import java.time.LocalTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import ar.dev.estebanrusch.reservation.model.entity.Reservation;
import ar.dev.estebanrusch.reservation.model.entity.ReservationStatus;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    boolean existsByDateAndTime(LocalDate date, LocalTime time);

    boolean existsByDateAndTimeAndStatus(LocalDate date, LocalTime time, ReservationStatus status);

    /**
     * Spring Data deriva esta query del nombre del método:
     *   WHERE LOWER(customer_name) LIKE LOWER('%search%')
     * El Pageable agrega LIMIT + OFFSET y devuelve Page con el total incluido.
     */
    Page<Reservation> findByCustomerNameContainingIgnoreCase(String search, Pageable pageable);
}
