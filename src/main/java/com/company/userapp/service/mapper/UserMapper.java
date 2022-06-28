package com.company.userapp.service.mapper;

import com.company.userapp.dto.model.UserDto;
import com.company.userapp.dto.response.UserResponse;

import com.company.userapp.exception.BadRequestException;

import com.company.userapp.model.User;

import com.company.userapp.util.JWTUtil;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.core.env.Environment;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.UUID;

@Component
public class UserMapper {

    private ObjectMapper objectMapper;

    private BCryptPasswordEncoder passwordEncoder;

    private Environment environment;

    public UserMapper(ObjectMapper objectMapper, BCryptPasswordEncoder passwordEncoder, Environment environment) {
        this.objectMapper = objectMapper;
        this.passwordEncoder = passwordEncoder;
        this.environment = environment;
    }

    public User toUser(UserDto userDto) {
        User user = objectMapper.convertValue(userDto, User.class);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setCreated(Instant.now());
        user.setModified(Instant.now());
        user.setActive(true);
        user.setLastLogin(user.getCreated());
        user.setToken(JWTUtil.generateJwtToken(user.getEmail(), environment.getProperty("user.secret.token", "SECRET_TOKEN_DEMO")));
        user.getPhones().forEach((phone) -> {
            phone.setUser(user);
        });

        return user;
    }

    public User updateEntity(User user, UserDto userDto) {

        user.setModified(Instant.now());
        user.setName(userDto.getName());
        user.setEmail(userDto.getEmail());
        user.setPassword(userDto.getPassword());
        user.setPhones(objectMapper.convertValue(userDto.getPhones(), new TypeReference<>(){}));
        user.getPhones().forEach((phone) -> {
            phone.setUser(user);
        });

        return user;
    }

    public UserResponse entityToUserResponse(User user) {

        return objectMapper.convertValue(user, UserResponse.class);
    }

    public UUID toUUID(String id) {
        try {
            return UUID.fromString(id);
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Id must have valid format");
        }
    }

}
