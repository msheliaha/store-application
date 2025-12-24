package org.example.storeapplication.config;

import lombok.RequiredArgsConstructor;
import org.example.storeapplication.repositories.ResetTokenRepository;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.LocalDateTime;

@Configuration
@EnableScheduling
@RequiredArgsConstructor
public class TokenCleanupConfig {

    private final ResetTokenRepository resetTokenRepository;

    @Scheduled(fixedRate = 3_600_000)
    public void clearExpiredTokens() {
        resetTokenRepository.deleteAllByExpireDateBefore(LocalDateTime.now());
    }
}
