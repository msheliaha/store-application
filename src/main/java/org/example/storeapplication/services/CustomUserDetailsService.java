package org.example.storeapplication.services;

import lombok.RequiredArgsConstructor;
import org.example.storeapplication.entities.User;
import org.example.storeapplication.repositories.UserRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    private final LoginAttemptService loginAttemptService;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        if(loginAttemptService.isBlocked()){
            throw new RuntimeException("blocked");
        }

        User user = userRepository.findById(email)
                .orElseThrow(() ->
                        new UsernameNotFoundException(email+ " not found"));

        Set<GrantedAuthority> authorities = Set.of();

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                authorities
        );
    }
}
