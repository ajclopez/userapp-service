package com.company.userapp.service;

import com.company.userapp.dto.model.UserDto;
import com.company.userapp.dto.response.UserResponse;
import com.company.userapp.exception.ConflictException;
import com.company.userapp.exception.InternalServerErrorException;
import com.company.userapp.exception.NotFoundException;
import com.company.userapp.model.User;
import com.company.userapp.repository.UserRepository;
import com.company.userapp.service.mapper.UserMapper;

import com.company.userapp.util.JWTUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private UserRepository userRepository;

    private UserMapper userMapper;

    @Autowired
    public UserService(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    public UserResponse create(UserDto userDto) {

        User user = userMapper.toUser(userDto);

        try {
            userRepository.save(user);
        } catch (DataIntegrityViolationException e) {
            if ( e.getMessage() != null && e.getMessage().toLowerCase().contains("unique_email_constraint") ) {
                throw new ConflictException("Email already registered");
            }

            throw new InternalServerErrorException("There was an internal server error");
        }

        return userMapper.entityToUserResponse(user);
    }

    public UserResponse findById(String id, String bearer) {

        User user = userRepository.findById(userMapper.toUUID(id)).orElseThrow(() -> {
            throw new NotFoundException(String.format("User with id %s not found", id));
        });

        JWTUtil.isValidToken(bearer, user.getToken());

        return userMapper.entityToUserResponse(user);
    }

    public UserResponse update(String id, UserDto userDto, String bearer) {

        User user = userRepository.findById(userMapper.toUUID(id)).orElseThrow(() -> {
            throw new NotFoundException(String.format("User with id %s not found", id));
        });

        JWTUtil.isValidToken(bearer, user.getToken());

        user = userMapper.updateEntity(user, userDto);

        try {

            return userMapper.entityToUserResponse(userRepository.save(user));

        } catch (DataIntegrityViolationException e) {

            if ( e.getMessage() != null && e.getMessage().toLowerCase().contains("unique_email_constraint") ) {
                throw new ConflictException("Email already registered");
            }

            throw new InternalServerErrorException("There was an internal server error");
        }
    }

    public void delete(String id, String bearer) {

        User user = userRepository.findById(userMapper.toUUID(id)).orElseThrow(() -> {
            throw new NotFoundException(String.format("User with id %s not found", id));
        });

        JWTUtil.isValidToken(bearer, user.getToken());

        userRepository.delete(user);
    }

}
