package org.example.storeapplication.controllers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.example.storeapplication.models.UserDTO;
import org.example.storeapplication.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class AuthController {

    public static final String LOGIN_PATH = "/api/v1/login";
    public static final String REGISTER_PATH = "/api/v1/register";
    public static final String LOGOUT_PATH = "/api/v1/logout";

    private final UserService userService;
    private final AuthenticationManager authenticationManager;

    private final SecurityContextLogoutHandler logoutHandler = new SecurityContextLogoutHandler();

    @PostMapping(REGISTER_PATH)
    public ResponseEntity registerUser(@RequestBody @Validated UserDTO userDTO){
        userService.registerUser(userDTO);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping(LOGIN_PATH)
    public ResponseEntity login(@RequestBody @Validated UserDTO userDTO, HttpServletRequest httpRequest){
        Authentication authenticationRequest =
                UsernamePasswordAuthenticationToken.unauthenticated(userDTO.getEmail(), userDTO.getPassword());
        Authentication authenticationResponse = this.authenticationManager.authenticate(authenticationRequest);

        SecurityContextHolder.getContext().setAuthentication(authenticationResponse);

        HttpSession session = httpRequest.getSession(true);
        session.setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());
        return ResponseEntity.ok(Map.of("sessionId", session.getId()));
    }

    @PostMapping(LOGOUT_PATH)
    public ResponseEntity logout(HttpServletRequest httpRequest, HttpServletResponse httpResponse, Authentication authentication){
        logoutHandler.logout(httpRequest, httpResponse, authentication);

        httpRequest.getSession().removeAttribute("SPRING_SECURITY_CONTEXT");
        httpRequest.getSession().invalidate();
        SecurityContextHolder.clearContext();
        return ResponseEntity.ok().build();
    }
}
