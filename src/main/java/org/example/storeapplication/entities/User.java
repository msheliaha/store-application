package org.example.storeapplication.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Entity
@Table(name = "user_table")
@Data
public class User {

    @Id
    @Email
    private String email;

    @NotNull
    private String password;

}
