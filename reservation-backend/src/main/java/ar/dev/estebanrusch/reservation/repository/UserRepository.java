package ar.dev.estebanrusch.reservation.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import ar.dev.estebanrusch.reservation.model.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);
}
