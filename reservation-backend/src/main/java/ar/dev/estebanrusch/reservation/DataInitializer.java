package ar.dev.estebanrusch.reservation;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import ar.dev.estebanrusch.reservation.model.entity.User;
import ar.dev.estebanrusch.reservation.repository.UserRepository;
import lombok.RequiredArgsConstructor;

/**
 * Crea el usuario inicial si la tabla está vacía.
 * Credencial por defecto: recepcion / recepcion123
 */
@Component
@RequiredArgsConstructor
public class DataInitializer implements ApplicationRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(ApplicationArguments args) {
        if (userRepository.count() > 0) {
            return;
        }

        User recepcion = new User();
        recepcion.setUsername("recepcion");
        recepcion.setPasswordHash(passwordEncoder.encode("recepcion123"));
        recepcion.setRole("RECEPTIONIST");
        userRepository.save(recepcion);
    }
}
