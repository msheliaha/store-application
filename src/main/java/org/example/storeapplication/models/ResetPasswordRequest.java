package org.example.storeapplication.models;

public record ResetPasswordRequest(
        String token,
        String newPassword
) {
}
