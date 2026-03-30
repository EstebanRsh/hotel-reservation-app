package ar.dev.estebanrusch.reservation.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;

import ar.dev.estebanrusch.reservation.exception.BusinessRuleViolationException;
import ar.dev.estebanrusch.reservation.model.dto.PageResponse;
import ar.dev.estebanrusch.reservation.model.dto.ReservationResponse;
import ar.dev.estebanrusch.reservation.model.entity.Reservation;
import ar.dev.estebanrusch.reservation.model.entity.ReservationStatus;
import ar.dev.estebanrusch.reservation.repository.ReservationRepository;

@ExtendWith(MockitoExtension.class)
class ReservationServiceTest {

    @Mock
    private ReservationRepository reservationRepository;

    @InjectMocks
    private ReservationService reservationService;

    // ── findReservations ────────────────────────────────────

    @Test
    void findReservations_sinFiltro_retornaTodos() {
        Reservation r1 = buildReservation(1L, "Ana García", ReservationStatus.ACTIVE);
        Reservation r2 = buildReservation(2L, "Juan López", ReservationStatus.CANCELLED);

        Page<Reservation> pageResult = new PageImpl<>(List.of(r1, r2));
        when(reservationRepository.findAll(any(Pageable.class))).thenReturn(pageResult);

        PageResponse<ReservationResponse> result = reservationService.findReservations("", 0, 10);

        assertThat(result.content()).hasSize(2);
        assertThat(result.totalElements()).isEqualTo(2);
        assertThat(result.content()).extracting(ReservationResponse::customerName)
                .containsExactly("Ana García", "Juan López");
    }

    @Test
    void findReservations_sinFiltro_tablaVacia_retornaListaVacia() {
        Page<Reservation> pageResult = new PageImpl<>(List.of());
        when(reservationRepository.findAll(any(Pageable.class))).thenReturn(pageResult);

        PageResponse<ReservationResponse> result = reservationService.findReservations("", 0, 10);

        assertThat(result.content()).isEmpty();
        assertThat(result.totalElements()).isZero();
    }

    @Test
    void findReservations_conFiltro_retornaSoloCoincidencias() {
        Reservation r1 = buildReservation(1L, "Ana García", ReservationStatus.ACTIVE);

        Page<Reservation> pageResult = new PageImpl<>(List.of(r1));
        when(reservationRepository.findByCustomerNameContainingIgnoreCase(eq("Ana"), any(Pageable.class)))
                .thenReturn(pageResult);

        PageResponse<ReservationResponse> result = reservationService.findReservations("Ana", 0, 10);

        assertThat(result.content()).hasSize(1);
        assertThat(result.content().get(0).customerName()).isEqualTo("Ana García");
    }

    // ── createReservation ───────────────────────────────────

    @Test
    void createReservation_validData_savesAndReturns() {
        LocalDate date = LocalDate.of(2026, 5, 10);
        LocalTime time = LocalTime.of(14, 0);

        when(reservationRepository.existsByDateAndTimeAndStatus(date, time, ReservationStatus.ACTIVE))
                .thenReturn(false);
        when(reservationRepository.save(any(Reservation.class)))
                .thenAnswer(inv -> {
                    Reservation r = inv.getArgument(0);
                    r.setId(1L);
                    return r;
                });

        Reservation result = reservationService.createReservation(
                "María Pérez", date, time, "Suite Presidencial");

        assertThat(result.getCustomerName()).isEqualTo("María Pérez");
        assertThat(result.getStatus()).isEqualTo(ReservationStatus.ACTIVE);
        verify(reservationRepository).save(any(Reservation.class));
    }

    @Test
    void createReservation_duplicateDateAndTime_throwsConflict() {
        LocalDate date = LocalDate.of(2026, 5, 10);
        LocalTime time = LocalTime.of(14, 0);

        when(reservationRepository.existsByDateAndTimeAndStatus(date, time, ReservationStatus.ACTIVE))
                .thenReturn(true);

        assertThatThrownBy(() ->
                reservationService.createReservation("Carlos Ruiz", date, time, "Spa, masajes y circuito termal"))
                .isInstanceOf(BusinessRuleViolationException.class)
                .hasMessageContaining("Another active reservation already exists");

        verify(reservationRepository, never()).save(any());
    }

    // ── cancelReservation ───────────────────────────────────

    @Test
    void cancelReservation_activeReservation_statusBecomesCANCELLED() {
        Reservation reservation = buildReservation(5L, "Pedro Martínez", ReservationStatus.ACTIVE);
        when(reservationRepository.findById(5L)).thenReturn(Optional.of(reservation));
        when(reservationRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        reservationService.cancelReservation(5L);

        assertThat(reservation.getStatus()).isEqualTo(ReservationStatus.CANCELLED);
        verify(reservationRepository).save(reservation);
    }

    @Test
    void cancelReservation_alreadyCancelled_throwsConflict() {
        Reservation reservation = buildReservation(6L, "Laura Sánchez", ReservationStatus.CANCELLED);
        when(reservationRepository.findById(6L)).thenReturn(Optional.of(reservation));

        assertThatThrownBy(() -> reservationService.cancelReservation(6L))
                .isInstanceOf(BusinessRuleViolationException.class)
                .hasMessageContaining("already cancelled");

        verify(reservationRepository, never()).save(any());
    }

    @Test
    void cancelReservation_notFound_throwsNotFound() {
        when(reservationRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> reservationService.cancelReservation(99L))
                .isInstanceOf(BusinessRuleViolationException.class)
                .satisfies(ex -> assertThat(((BusinessRuleViolationException) ex).getHttpStatus())
                        .isEqualTo(HttpStatus.NOT_FOUND));
    }

    // ── helpers ─────────────────────────────────────────────

    private Reservation buildReservation(Long id, String customerName, ReservationStatus status) {
        Reservation r = new Reservation();
        r.setId(id);
        r.setCustomerName(customerName);
        r.setDate(LocalDate.of(2026, 4, 1));
        r.setTime(LocalTime.of(10, 0));
        r.setService("Habitación Deluxe con balcón");
        r.setStatus(status);
        return r;
    }
}
