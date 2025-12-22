package org.example.storeapplication.services;

import org.example.storeapplication.models.UserDTO;

import java.util.Optional;

public interface UserService {

    UserDTO registerUser(UserDTO userDTO);

}
