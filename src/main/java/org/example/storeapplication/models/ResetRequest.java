package org.example.storeapplication.models;

import jakarta.validation.constraints.Email;

public record ResetRequest(@Email String email) {
}
