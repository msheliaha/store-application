package org.example.storeapplication.services;


import com.google.common.cache.LoadingCache;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;

@Service
@RequiredArgsConstructor
public class LoginAttemptServiceImpl implements LoginAttemptService {

    public static final int MAX_ATTEMPT = 10;

    private final LoadingCache<String, Integer> attemptsCache;
    private final HttpServletRequest request;



    @Override
    public void loginFailed(final String key) {
        int attempts;
        try {
            attempts = attemptsCache.get(key);
        } catch (final ExecutionException e) {
            attempts = 0;
        }
        attempts++;
        attemptsCache.put(key, attempts);
    }

    @Override
    public boolean isBlocked() {
        try {
            return attemptsCache.get(getClientIP()) >= MAX_ATTEMPT;
        } catch (final ExecutionException e) {
            return false;
        }
    }

    private String getClientIP() {
        final String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null || xfHeader.isEmpty() || !xfHeader.contains(request.getRemoteAddr())) {
            return request.getRemoteAddr();
        }
        return xfHeader.split(",")[0];
    }
}
