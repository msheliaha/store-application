package org.example.storeapplication.events;

import lombok.Data;

public record ResetPasswordEvent (
    String email,
    String resetToken
){
}
