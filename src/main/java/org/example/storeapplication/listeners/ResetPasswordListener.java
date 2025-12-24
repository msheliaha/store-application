package org.example.storeapplication.listeners;

import org.example.storeapplication.controllers.PasswordResetController;
import org.example.storeapplication.events.ResetPasswordEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class ResetPasswordListener {

    @Value("${app.base-url}")
    private String basePath;

    @EventListener
    public void listen(ResetPasswordEvent event){

        // Sending link to reset password on user email
        String fullPath = basePath + PasswordResetController.RESET_PATH + "?token=";
        String resetUrl = fullPath+event.resetToken();
        System.out.println("Link to reset password for "+event.email());
        System.out.println(resetUrl);


    }
}
