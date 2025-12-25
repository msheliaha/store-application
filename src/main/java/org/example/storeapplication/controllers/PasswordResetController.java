package org.example.storeapplication.controllers;

import lombok.RequiredArgsConstructor;
import org.example.storeapplication.models.ResetPasswordRequest;
import org.example.storeapplication.models.ResetRequest;
import org.example.storeapplication.services.PasswordResetService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class PasswordResetController {

    public static final String RESET_PATH = "/api/v1/reset-password";
    public static final String RESET_PATH_CONFIRM = RESET_PATH+"/confirm";

    private final PasswordResetService passwordResetService;

    @PostMapping(RESET_PATH)
    public ResponseEntity requestReset(@RequestBody @Validated ResetRequest request){
        passwordResetService.processRequest(request.email());
        return ResponseEntity.ok("Reset link sent to your email");
    }

    @GetMapping(RESET_PATH)
    public ResponseEntity validateToken(@RequestParam("token") String token){
        if(!passwordResetService.validateToken(token)){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid or expired token");
        }
        return ResponseEntity.ok("Token is valid");
    }

    @PostMapping(RESET_PATH_CONFIRM)
    public ResponseEntity resetPassword(@RequestBody ResetPasswordRequest request){
        if(!passwordResetService.resetPassword(request)){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid or expired token");
        }
        return ResponseEntity.ok("Password updated");
    }


}
