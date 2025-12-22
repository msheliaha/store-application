package org.example.storeapplication.models;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UserDTO {
    @Email
    private String email;

    @NotNull
    private String password;
}
