package org.example.storeapplication.services;

import org.example.storeapplication.entities.User;
import org.example.storeapplication.exception.UserExistsException;
import org.example.storeapplication.mappers.UserMapper;
import org.example.storeapplication.models.UserDTO;
import org.example.storeapplication.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceJpaImplTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserServiceJpaImpl userService;

    @Test
    void registerUser_WhenUserNew_ShouldEncodePasswordAndSave() {
        String rawPassword = "password";
        String encodedPassword = "encodedPassword";
        String email = "test@example.com";

        UserDTO inputDto = new UserDTO();
        inputDto.setEmail(email);
        inputDto.setPassword(rawPassword);

        User savedUser = new User();
        savedUser.setEmail(email);
        savedUser.setPassword(encodedPassword);

        UserDTO outputDto = new UserDTO();
        outputDto.setEmail(email);

        when(userRepository.existsById(email)).thenReturn(false);
        when(passwordEncoder.encode(rawPassword)).thenReturn(encodedPassword);
        when(userRepository.save(savedUser)).thenReturn(savedUser);
        when(userMapper.userToUserDto(savedUser)).thenReturn(outputDto);

        UserDTO result = userService.registerUser(inputDto);

        assertNotNull(result);
        assertEquals(email, result.getEmail());

        verify(passwordEncoder).encode(rawPassword);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void registerUser_WhenUserExists_ShouldThrowException() {
        String email = "existing@example.com";
        UserDTO inputDto = new UserDTO();
        inputDto.setEmail(email);
        inputDto.setPassword("123");

        when(userRepository.existsById(email)).thenReturn(true);

        assertThrows(UserExistsException.class, () -> {
            userService.registerUser(inputDto);
        });

        verify(userRepository, never()).save(any());
        verify(passwordEncoder, never()).encode(any());
    }

}