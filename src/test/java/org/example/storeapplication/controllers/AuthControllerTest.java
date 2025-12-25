package org.example.storeapplication.controllers;

import org.example.storeapplication.config.SecurityConfig;
import org.example.storeapplication.models.UserDTO;
import org.example.storeapplication.services.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(AuthController.class)
@Import(SecurityConfig.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private AuthenticationManager authenticationManager;

    @Test
    void registerUser_Success_ShouldReturnsCreated() throws Exception {
        UserDTO userDTO = new UserDTO();
        userDTO.setEmail("new@user.com");
        userDTO.setPassword("password123");

        mockMvc.perform(post(AuthController.REGISTER_PATH)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDTO)))
                .andExpect(status().isCreated());

        verify(userService).registerUser(userDTO);
    }


    @Test
    void login_Success_ShouldReturnId() throws Exception {
        String email = "test@user.com";
        String pass = "pass";
        UserDTO loginRequest = new UserDTO();
        loginRequest.setEmail(email);
        loginRequest.setPassword(pass);

        Authentication mockAuth = mock(Authentication.class);
        when(mockAuth.isAuthenticated()).thenReturn(true);
        when(authenticationManager.authenticate(mockAuth)).thenReturn(mockAuth);

        mockMvc.perform(post(AuthController.LOGIN_PATH)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sessionId").exists())
                .andExpect(result -> {
                    MockHttpSession session = (MockHttpSession) result.getRequest().getSession(false);
                    assertNotNull(session);
                    assertNotNull(session.getAttribute("SPRING_SECURITY_CONTEXT"));
                });
    }

    @Test
    void login_BadCredentials_ShouldResponseForbidden() throws Exception {
        UserDTO loginRequest = new UserDTO();
        loginRequest.setEmail("wrong@user.com");
        loginRequest.setPassword("wrongpass");

        Authentication authentication =
                UsernamePasswordAuthenticationToken.unauthenticated(loginRequest.getEmail(), loginRequest.getPassword());

        when(authenticationManager.authenticate(authentication)).thenThrow(new BadCredentialsException("Bad creds"));


        mockMvc.perform(post(AuthController.LOGIN_PATH)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "example@mail.com")
    void logout_ShouldInvalidateSession() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("SPRING_SECURITY_CONTEXT", "some context");

        mockMvc.perform(post(AuthController.LOGOUT_PATH)
                        .session(session)
                        .with(csrf()))
                .andExpect(status().isOk());

        assertTrue(session.isInvalid());
    }

}