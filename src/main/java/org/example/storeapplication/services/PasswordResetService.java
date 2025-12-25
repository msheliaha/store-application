package org.example.storeapplication.services;

import org.example.storeapplication.models.ResetPasswordRequest;

public interface PasswordResetService {

    void processRequest(String userEmail);

    boolean resetPassword(ResetPasswordRequest request);

    boolean validateToken(String token);
}
