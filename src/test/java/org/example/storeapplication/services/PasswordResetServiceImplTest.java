package org.example.storeapplication.services;

import org.example.storeapplication.config.SecurityConfig;
import org.example.storeapplication.config.TokenCleanupConfig;
import org.example.storeapplication.entities.ResetToken;
import org.example.storeapplication.entities.User;
import org.example.storeapplication.events.ResetPasswordEvent;
import org.example.storeapplication.models.ResetPasswordRequest;
import org.example.storeapplication.repositories.ResetTokenRepository;
import org.example.storeapplication.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PasswordResetServiceImplTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private ResetTokenRepository resetTokenRepository;

    @Mock
    private ApplicationEventPublisher publisher;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private PasswordResetServiceImpl service;


    @Test
    void processRequest_WhenUserNotExists_ShouldDoNothing() {

        String email = "unknown@example.com";
        when(userRepository.findById(email)).thenReturn(Optional.empty());

        service.processRequest(email);

        verify(resetTokenRepository, never()).save(any());
        verify(publisher, never()).publishEvent(any());
    }

    @Test
    void processRequest_WhenUserExists_ShouldSaveTokenAndPublishEvent() {
        String email = "user@example.com";
        User mockUser = new User();
        mockUser.setEmail(email);

        when(userRepository.findById(email)).thenReturn(Optional.of(mockUser));

        service.processRequest(email);

        ArgumentCaptor<ResetToken> tokenCaptor = ArgumentCaptor.forClass(ResetToken.class);
        verify(resetTokenRepository).save(tokenCaptor.capture());

        ResetToken savedToken = tokenCaptor.getValue();
        assertNotNull(savedToken.getToken());
        assertEquals(mockUser, savedToken.getUser());
        assertTrue(savedToken.getExpireDate().isAfter(LocalDateTime.now()));

        verify(publisher).publishEvent(any(ResetPasswordEvent.class));
    }


    @Test
    void resetPassword_WhenTokenNotExists_ShouldReturnFalse() {

        ResetPasswordRequest request = new ResetPasswordRequest("invalid-token", "newPass");
        when(resetTokenRepository.findByToken(request.token())).thenReturn(Optional.empty());

        boolean result = service.resetPassword(request);

        assertFalse(result);
        verify(userRepository, never()).save(any());
    }

    @Test
    void resetPassword_WhenTokenExpired_ShouldReturnFalse() {

        String tokenStr = "expired-token";
        ResetPasswordRequest request = new ResetPasswordRequest(tokenStr, "newPass");

        ResetToken expiredToken = new ResetToken();
        expiredToken.setToken(tokenStr);

        expiredToken.setExpireDate(LocalDateTime.now().minusMinutes(1));

        when(resetTokenRepository.findByToken(tokenStr)).thenReturn(Optional.of(expiredToken));

        boolean result = service.resetPassword(request);

        assertFalse(result);
        verify(userRepository, never()).save(any());
    }

    @Test
    void resetPassword_WhenTokenIsValid_ShouldUpdatePassword() {

        String token = "valid-token";
        String newPass = "secret123";
        String encodedPass = "encoded-secret123";
        ResetPasswordRequest request = new ResetPasswordRequest(token, newPass);

        User user = new User();
        ResetToken validToken = new ResetToken();
        validToken.setToken(token);
        validToken.setUser(user);
        validToken.setExpireDate(LocalDateTime.now().plusMinutes(30));

        when(resetTokenRepository.findByToken(token)).thenReturn(Optional.of(validToken));
        when(passwordEncoder.encode(newPass)).thenReturn(encodedPass);

        boolean result = service.resetPassword(request);

        assertTrue(result);
        assertEquals(encodedPass, user.getPassword());
        verify(resetTokenRepository).delete(validToken);

    }

    @Test
    void validateToken_WhenTokenIsValid_ShouldReturnTrue() {

        String tokenStr = "valid";
        ResetToken token = new ResetToken();
        token.setExpireDate(LocalDateTime.now().plusMinutes(10));

        when(resetTokenRepository.findByToken(tokenStr)).thenReturn(Optional.of(token));

        boolean result = service.validateToken(tokenStr);

        assertTrue(result);
    }

    @Test
    void validateToken_WhenTokenExpired_ShouldReturnFalse() {

        String tokenStr = "expired";
        ResetToken token = new ResetToken();
        token.setExpireDate(LocalDateTime.now().minusMinutes(10));

        when(resetTokenRepository.findByToken(tokenStr)).thenReturn(Optional.of(token));

        boolean result = service.validateToken(tokenStr);

        assertFalse(result);
    }

    @Test
    void validateToken_WhenTokenDoesNotExist_ShouldReturnFalse() {
        when(resetTokenRepository.findByToken("missing")).thenReturn(Optional.empty());

        boolean result = service.validateToken("missing");

        assertFalse(result);
    }
}