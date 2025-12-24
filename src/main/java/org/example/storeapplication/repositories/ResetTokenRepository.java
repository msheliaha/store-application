package org.example.storeapplication.repositories;

import org.example.storeapplication.entities.ResetToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface ResetTokenRepository extends JpaRepository<ResetToken, Long> {
    Optional<ResetToken> findByToken(String token);

    void deleteAllByExpireDateBefore(LocalDateTime expiresAtBefore);
}
