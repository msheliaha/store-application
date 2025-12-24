package org.example.storeapplication.services;

public interface LoginAttemptService {
    void loginFailed(String remoteAddr);
    boolean isBlocked();
}
