package com.company.userapp.security;

import com.company.userapp.model.User;
import com.company.userapp.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.ArrayList;

@Component
public class CustomAuthenticationProvider implements AuthenticationProvider {

    private UserRepository userRepository;

    private BCryptPasswordEncoder passwordEncoder;

    public CustomAuthenticationProvider(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }


    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        String username = authentication.getName().toLowerCase();
        String password = authentication.getCredentials() == null ? "" : authentication.getCredentials().toString();

        if( username.isEmpty() || password.isEmpty() ) {
            throw new BadCredentialsException("Authentication failed");
        }

        User user = userRepository.findByEmail(username);
        if( user == null ) {
            throw new BadCredentialsException("Authentication failed");
        } else if( passwordEncoder.matches(password, user.getPassword()) && user.getActive() ) {

            user.setLastLogin(Instant.now());
            userRepository.save(user);
            return new UsernamePasswordAuthenticationToken(username, password, new ArrayList<>());
        } else {
            throw new BadCredentialsException("Authentication failed");
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }

}
