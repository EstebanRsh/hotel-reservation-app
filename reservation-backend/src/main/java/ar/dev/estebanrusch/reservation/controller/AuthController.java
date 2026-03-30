package ar.dev.estebanrusch.reservation.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ar.dev.estebanrusch.reservation.config.JwtUtil;
import ar.dev.estebanrusch.reservation.exception.BusinessRuleViolationException;
import ar.dev.estebanrusch.reservation.model.dto.LoginRequest;
import ar.dev.estebanrusch.reservation.model.dto.LoginResponse;
import ar.dev.estebanrusch.reservation.model.entity.User;
import ar.dev.estebanrusch.reservation.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.username(), request.password()));

            String username = authentication.getName();
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new BusinessRuleViolationException("Usuario no encontrado", HttpStatus.NOT_FOUND));

            String token = jwtUtil.generateToken(username);
            return ResponseEntity.ok(new LoginResponse(token, username, user.getRole()));

        } catch (BadCredentialsException e) {
            throw new BusinessRuleViolationException("Credenciales incorrectas", HttpStatus.UNAUTHORIZED);
        }
    }
}
