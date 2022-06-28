package com.company.userapp.security;

import com.company.userapp.dto.model.AppUserDto;
import com.company.userapp.model.User;
import com.company.userapp.repository.UserRepository;
import com.company.userapp.util.JWTUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;

public class JWTAuthenticationFilter  extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;

    private UserRepository userRepository;

    public JWTAuthenticationFilter(String pattern, AuthenticationManager authenticationManager, UserRepository userRepository) {
        this.authenticationManager = authenticationManager;
        this.setRequiresAuthenticationRequestMatcher(new AntPathRequestMatcher(pattern, "POST"));
        this.userRepository = userRepository;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request,
                                                HttpServletResponse response) throws AuthenticationException {
        try {
            AppUserDto user = new ObjectMapper().readValue(request.getInputStream(), AppUserDto.class);

            return authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword(), new ArrayList<>()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request,
                                            HttpServletResponse response,
                                            FilterChain chain,
                                            Authentication auth) {

        if ( auth.getPrincipal() != null ) {
            String email = auth.getPrincipal().toString();
            User userByEmail = userRepository.findByEmail(email);

            if ( userByEmail != null  && !userByEmail.getEmail().isEmpty() ) {
                response.addHeader(JWTUtil.HEADER_AUTHORIZATION, JWTUtil.TOKEN_PREFIX + userByEmail.getToken());
            }
        }
    }

}
