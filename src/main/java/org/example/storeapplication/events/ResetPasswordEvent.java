package org.example.storeapplication.events;


public record ResetPasswordEvent (
    String email,
    String resetToken
){
}
