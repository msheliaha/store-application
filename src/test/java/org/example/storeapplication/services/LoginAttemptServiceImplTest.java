package org.example.storeapplication.services;

import com.google.common.cache.LoadingCache;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LoginAttemptServiceImplTest {

    @Mock
    private LoadingCache<String, Integer> attemptsCache;

    @Mock
    private HttpServletRequest request;

    @InjectMocks
    private LoginAttemptServiceImpl loginAttemptService;

    private final String IP_ADDRESS = "192.168.1.1";


    @Test
    void loginFailed_ShouldIncrementAttempts() throws ExecutionException {
        when(attemptsCache.get(IP_ADDRESS)).thenReturn(2);

        loginAttemptService.loginFailed(IP_ADDRESS);

        verify(attemptsCache).put(IP_ADDRESS, 3);
    }

    @Test
    void loginFailed_ShouldHandleCacheException() throws ExecutionException {
        when(attemptsCache.get(IP_ADDRESS)).thenThrow(new ExecutionException(new Throwable()));

        loginAttemptService.loginFailed(IP_ADDRESS);

        verify(attemptsCache).put(IP_ADDRESS, 1);
    }

    @Test
    void isBlocked_WhenMaxAttempts_ShouldReturnTrue() throws ExecutionException {
        when(request.getHeader("X-Forwarded-For")).thenReturn(null);
        when(request.getRemoteAddr()).thenReturn(IP_ADDRESS);

        when(attemptsCache.get(IP_ADDRESS)).thenReturn(LoginAttemptServiceImpl.MAX_ATTEMPT);

        boolean blocked = loginAttemptService.isBlocked();

        assertTrue(blocked);
    }

    @Test
    void isBlocked_WhenAttemptsNotEnough_ShouldReturnFalse() throws ExecutionException {
        when(request.getHeader("X-Forwarded-For")).thenReturn(null);
        when(request.getRemoteAddr()).thenReturn(IP_ADDRESS);

        when(attemptsCache.get(IP_ADDRESS)).thenReturn(5);

        boolean blocked = loginAttemptService.isBlocked();

        assertFalse(blocked);
    }

}