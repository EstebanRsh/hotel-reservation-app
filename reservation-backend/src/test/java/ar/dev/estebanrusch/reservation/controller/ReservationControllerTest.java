package ar.dev.estebanrusch.reservation.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import ar.dev.estebanrusch.reservation.config.JwtUtil;
import ar.dev.estebanrusch.reservation.exception.BusinessRuleViolationException;
import ar.dev.estebanrusch.reservation.model.dto.PageResponse;
import ar.dev.estebanrusch.reservation.model.dto.ReservationResponse;
import ar.dev.estebanrusch.reservation.model.entity.Reservation;
import ar.dev.estebanrusch.reservation.model.entity.ReservationStatus;
import ar.dev.estebanrusch.reservation.service.ReservationService;

@SpringBootTest
@AutoConfigureMockMvc
class ReservationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtUtil jwtUtil;

    @MockitoBean
    private ReservationService reservationService;

    private String validToken;

    @BeforeEach
    void setUp() {
        validToken = jwtUtil.generateToken("recepcion");
    }

    // ── GET /reservations ───────────────────────────────────

    @Test
    void listReservations_authenticated_returns200WithList() throws Exception {
        Reservation r = buildReservation(1L, "Ana García", ReservationStatus.ACTIVE);
        PageResponse<ReservationResponse> pageResponse = new PageResponse<>(
                List.of(ReservationResponse.from(r)), 0, 10, 1L, 1);

        when(reservationService.findReservations(any(), anyInt(), anyInt())).thenReturn(pageResponse);

        mockMvc.perform(get("/reservations")
                        .header("Authorization", "Bearer " + validToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].customerName").value("Ana García"))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    void listReservations_unauthenticated_returns401() throws Exception {
        mockMvc.perform(get("/reservations"))
                .andExpect(status().isUnauthorized());
    }

    // ── POST /reservations ──────────────────────────────────

    @Test
    void createReservation_validPayload_returns201() throws Exception {
        Reservation saved = buildReservation(2L, "Carlos López", ReservationStatus.ACTIVE);
        when(reservationService.createReservation(
                eq("Carlos López"), any(LocalDate.class), any(LocalTime.class), any()))
                .thenReturn(saved);

        String body = """
                {
                  "customerName": "Carlos López",
                  "date": "2026-05-10",
                  "time": "14:00:00",
                  "service": "Suite Presidencial"
                }
                """;

        mockMvc.perform(post("/reservations")
                        .header("Authorization", "Bearer " + validToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(2))
                .andExpect(jsonPath("$.customerName").value("Carlos López"));
    }

    @Test
    void createReservation_missingCustomerName_returns400() throws Exception {
        String body = """
                {
                  "date": "2026-05-10",
                  "time": "14:00:00",
                  "service": "Suite Presidencial"
                }
                """;

        mockMvc.perform(post("/reservations")
                        .header("Authorization", "Bearer " + validToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createReservation_duplicateSlot_returns409() throws Exception {
        when(reservationService.createReservation(any(), any(), any(), any()))
                .thenThrow(new BusinessRuleViolationException(
                        "Another active reservation already exists for the same date and time",
                        HttpStatus.CONFLICT));

        String body = """
                {
                  "customerName": "Laura Pérez",
                  "date": "2026-05-10",
                  "time": "14:00:00",
                  "service": "Spa, masajes y circuito termal"
                }
                """;

        mockMvc.perform(post("/reservations")
                        .header("Authorization", "Bearer " + validToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value(
                        "Another active reservation already exists for the same date and time"));
    }

    // ── DELETE /reservations/{id} ───────────────────────────

    @Test
    void cancelReservation_existingActive_returns204() throws Exception {
        doNothing().when(reservationService).cancelReservation(1L);

        mockMvc.perform(delete("/reservations/1")
                        .header("Authorization", "Bearer " + validToken))
                .andExpect(status().isNoContent());
    }

    @Test
    void cancelReservation_notFound_returns404() throws Exception {
        doThrow(new BusinessRuleViolationException("Reservation not found with id: 99", HttpStatus.NOT_FOUND))
                .when(reservationService).cancelReservation(99L);

        mockMvc.perform(delete("/reservations/99")
                        .header("Authorization", "Bearer " + validToken))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Reservation not found with id: 99"));
    }

    // ── helpers ─────────────────────────────────────────────

    private Reservation buildReservation(Long id, String customerName, ReservationStatus status) {
        Reservation r = new Reservation();
        r.setId(id);
        r.setCustomerName(customerName);
        r.setDate(LocalDate.of(2026, 5, 10));
        r.setTime(LocalTime.of(14, 0));
        r.setService("Suite Presidencial");
        r.setStatus(status);
        return r;
    }
}
