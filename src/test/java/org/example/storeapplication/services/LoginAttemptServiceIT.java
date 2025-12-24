package org.example.storeapplication.services;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class LoginAttemptServiceIT {

    @Autowired
    private LoginAttemptService service;

    @MockitoBean
    private HttpServletRequest request;


    @Test
    void shouldBlockUserAfterMaxAttempts() {
        String ip = "1.2.3.4";
        when(request.getHeader("X-Forwarded-For")).thenReturn(null);
        when(request.getRemoteAddr()).thenReturn(ip);

        for (int i = 0; i < 10; i++) {
            service.loginFailed(ip);
        }

        assertTrue(service.isBlocked());
    }
}