package ar.dev.estebanrusch.reservation.service;

import java.time.LocalDate;
import java.time.LocalTime;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import ar.dev.estebanrusch.reservation.model.dto.PageResponse;
import ar.dev.estebanrusch.reservation.model.dto.ReservationResponse;
import ar.dev.estebanrusch.reservation.exception.BusinessRuleViolationException;
import ar.dev.estebanrusch.reservation.model.entity.Reservation;
import ar.dev.estebanrusch.reservation.model.entity.ReservationStatus;
import ar.dev.estebanrusch.reservation.repository.ReservationRepository;

@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;

    public ReservationService(ReservationRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
    }

    public PageResponse<ReservationResponse> findReservations(String search, int page, int size) {
        PageRequest pageable = PageRequest.of(page, size,
                Sort.by("date").descending().and(Sort.by("time").descending()));

        Page<Reservation> result = (search == null || search.isBlank())
                ? reservationRepository.findAll(pageable)
                : reservationRepository.findByCustomerNameContainingIgnoreCase(search.trim(), pageable);

        return PageResponse.from(result, ReservationResponse::from);
    }

    @Transactional
    public Reservation createReservation(
            String customerName, LocalDate date, LocalTime time, String service) {
        if (reservationRepository.existsByDateAndTimeAndStatus(date, time, ReservationStatus.ACTIVE)) {
            throw new BusinessRuleViolationException(
                    "Another active reservation already exists for the same date and time",
                    HttpStatus.CONFLICT);
        }
        Reservation reservation = new Reservation();
        reservation.setCustomerName(customerName);
        reservation.setDate(date);
        reservation.setTime(time);
        reservation.setService(service);
        reservation.setStatus(ReservationStatus.ACTIVE);
        return reservationRepository.save(reservation);
    }

    @Transactional
    public void cancelReservation(Long id) {
        Reservation reservation = reservationRepository
                .findById(id)
                .orElseThrow(() -> new BusinessRuleViolationException(
                        "Reservation not found with id: " + id, HttpStatus.NOT_FOUND));
        if (reservation.getStatus() == ReservationStatus.CANCELLED) {
            throw new BusinessRuleViolationException("Reservation is already cancelled", HttpStatus.CONFLICT);
        }
        reservation.setStatus(ReservationStatus.CANCELLED);
        reservationRepository.save(reservation);
    }
}
