package org.example.storeapplication.services;

import lombok.RequiredArgsConstructor;
import org.example.storeapplication.entities.ResetToken;
import org.example.storeapplication.entities.User;
import org.example.storeapplication.events.ResetPasswordEvent;
import org.example.storeapplication.models.ResetPasswordRequest;
import org.example.storeapplication.repositories.ResetTokenRepository;
import org.example.storeapplication.repositories.UserRepository;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PasswordResetServiceImpl implements PasswordResetService {

    private static final SecureRandom secureRandom = new SecureRandom();
    private static final Base64.Encoder base64Encoder =
            Base64.getUrlEncoder().withoutPadding();

    private final UserRepository userRepository;
    private final ResetTokenRepository resetTokenRepository;
    private final ApplicationEventPublisher publisher;
    private final PasswordEncoder passwordEncoder;


    public String makeResetToken() {
        byte[] randomBytes = new byte[32];
        secureRandom.nextBytes(randomBytes);
        return base64Encoder.encodeToString(randomBytes);
    }

    @Override
    public void processRequest(String userEmail) {
        Optional<User> userOpt = userRepository.findById(userEmail);
        if (userOpt.isEmpty()) return;

        User user = userOpt.get();
        String token = makeResetToken();

        ResetToken record = new ResetToken();
        record.setToken(token);
        record.setUser(user);
        record.setExpireDate(LocalDateTime.now().plusMinutes(30));

        resetTokenRepository.save(record);

        publisher.publishEvent(new ResetPasswordEvent(userEmail, token));
    }

    @Override
    @Transactional
    public boolean resetPassword(ResetPasswordRequest request) {
        Optional<ResetToken> tokenOpt = resetTokenRepository.findByToken(request.token());

        if (tokenOpt.isEmpty() || tokenOpt.get().isExpired()) {
            return false;
        }

        ResetToken tokenRecord = tokenOpt.get();
        User user = tokenRecord.getUser();

        user.setPassword(passwordEncoder.encode(request.newPassword()));
        resetTokenRepository.delete(tokenRecord);

        return true;
    }

    @Override
    public boolean validateToken(String token) {
        Optional<ResetToken> tokenOpt = resetTokenRepository.findByToken(token);
        return tokenOpt.isPresent() && !tokenOpt.get().isExpired();
    }
}
