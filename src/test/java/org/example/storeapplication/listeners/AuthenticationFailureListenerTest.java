package org.example.storeapplication.listeners;

import jakarta.servlet.http.HttpServletRequest;
import org.example.storeapplication.services.LoginAttemptService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthenticationFailureListenerTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private LoginAttemptService loginAttemptService;

    @InjectMocks
    private AuthenticationFailureListener listener;

    @Mock
    private AuthenticationFailureBadCredentialsEvent event;

    @Test
    void onApplicationEvent_WhenHeaderMissing_ShouldExtractRemoteAddr() {

        String remoteIp = "ip";
        when(request.getHeader("X-Forwarded-For")).thenReturn(null);
        when(request.getRemoteAddr()).thenReturn(remoteIp);

        listener.onApplicationEvent(event);

        verify(loginAttemptService).loginFailed(remoteIp);
    }

    @Test
    void onApplicationEvent_WhenPresent_ShouldExtractValue() {
        String realIp = "real-ip";
        String headerValue = realIp + ", not-real";

        when(request.getHeader("X-Forwarded-For")).thenReturn(headerValue);
        when(request.getRemoteAddr()).thenReturn(realIp);

        listener.onApplicationEvent(event);

        verify(loginAttemptService).loginFailed(realIp);
    }

}