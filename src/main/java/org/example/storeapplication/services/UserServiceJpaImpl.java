package org.example.storeapplication.services;

import lombok.RequiredArgsConstructor;
import org.example.storeapplication.entities.User;
import org.example.storeapplication.exception.UserExistsException;
import org.example.storeapplication.mappers.UserMapper;
import org.example.storeapplication.models.UserDTO;
import org.example.storeapplication.repositories.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceJpaImpl implements UserService{

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    @Transactional
    @Override
    public UserDTO registerUser(UserDTO userDTO) {
        if(userRepository.existsById(userDTO.getEmail())){
            throw new UserExistsException();
        }
        User user = new User();
        user.setEmail(userDTO.getEmail());
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));

        return userMapper.userToUserDto(userRepository.save(user));
    }
}
