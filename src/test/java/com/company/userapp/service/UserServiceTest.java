package com.company.userapp.service;

import com.company.userapp.dto.model.PhoneDto;
import com.company.userapp.dto.model.UserDto;
import com.company.userapp.dto.response.UserResponse;

import com.company.userapp.exception.BadRequestException;
import com.company.userapp.exception.ConflictException;
import com.company.userapp.exception.InternalServerErrorException;
import com.company.userapp.exception.NotFoundException;
import com.company.userapp.model.Phone;
import com.company.userapp.model.User;
import com.company.userapp.repository.UserRepository;
import com.company.userapp.service.mapper.UserMapper;

import com.company.userapp.util.JWTUtil;
import com.company.userapp.util.UtilTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.Instant;
import java.util.*;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserService userService;

    private UserDto userDto;

    private User user;

    private UserResponse userResponse;

    @BeforeEach
    public void setup() {
        userDto = UtilTest.buildUserDto();
        user = UtilTest.buildUser(userDto);
        userResponse =  UtilTest.buildUserResponse(user, userDto.getPhones());
    }

    @Test
    public void givenCreateNewUserThatUserDtoThenReturnUserResponse() {

        Mockito.when(userMapper.toUser(userDto)).thenReturn(user);
        Mockito.when(userRepository.save(user)).thenReturn(user);
        Mockito.when(userMapper.entityToUserResponse(user)).thenReturn(userResponse);

        UserResponse response = userService.create(userDto);

        Assertions.assertEquals(response.getId(), user.getId().toString());
        Assertions.assertEquals(response.getName(), user.getName());
        Assertions.assertEquals(response.getEmail(), user.getEmail());
    }

    @Test
    public void givenCreateNewUserThatEmailAlreadyExistsThenThrowsConflictException() {

        Mockito.when(userMapper.toUser(userDto)).thenReturn(user);

        Assertions.assertThrows(ConflictException.class, () -> {
            Mockito.when(userRepository.save(user)).thenThrow(new DataIntegrityViolationException("unique_email_constraint"));
            userService.create(userDto);
        });

    }

    @Test
    public void givenCreateNewUserThatFailSavingUserThenThrowsInternalServerErrorException() {

        Mockito.when(userMapper.toUser(userDto)).thenReturn(user);

        Assertions.assertThrows(InternalServerErrorException.class, () -> {
            Mockito.when(userRepository.save(user)).thenThrow(DataIntegrityViolationException.class);
            userService.create(userDto);
        });

    }

    @Test
    public void givenFindByIdThatValidIdThenReturnUserResponse() {

        Mockito.when(userMapper.toUUID(user.getId().toString())).thenReturn(user.getId());
        Mockito.when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        Mockito.when(userMapper.entityToUserResponse(user)).thenReturn(userResponse);

        UserResponse response = userService.findById(user.getId().toString(), user.getToken());

        Assertions.assertEquals(response.getId(), userResponse.getId());
        Assertions.assertEquals(response.getEmail(), userResponse.getEmail());
    }

    @Test
    public void givenFindByIdThatInvalidIdThenReturnBadRequestException() {

        Mockito.when(userMapper.toUUID(user.getId().toString())).thenThrow(BadRequestException.class);

        Assertions.assertThrows(BadRequestException.class, () -> {
            userService.findById(user.getId().toString(), user.getToken());
        });
    }

    @Test
    public void givenFindByIdThatNotFoundIdThenReturnNotFoundException() {

        Mockito.when(userMapper.toUUID(user.getId().toString())).thenReturn(user.getId());
        Mockito.when(userRepository.findById(UUID.randomUUID())).thenThrow(NotFoundException.class);

        Assertions.assertThrows(NotFoundException.class, () -> {
            userService.findById(user.getId().toString(), user.getToken());
        });
    }

    @Test
    public void givenUpdateUserThatUserDtoThenUpdateSuccessfully() {

        Mockito.when(userMapper.toUUID(user.getId().toString())).thenReturn(user.getId());
        Mockito.when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        Mockito.when(userMapper.updateEntity(user, userDto)).thenReturn(user);
        Mockito.when(userRepository.save(user)).thenReturn(user);
        Mockito.when(userMapper.entityToUserResponse(user)).thenReturn(userResponse);

        UserResponse response = userService.update(user.getId().toString(), userDto, user.getToken());

        Assertions.assertEquals(response.getId(), userResponse.getId());
        Assertions.assertEquals(response.getEmail(), userResponse.getEmail());

    }

    @Test
    public void givenUpdateThatNotFoundIdThenReturnNotFoundException() {

        Mockito.when(userMapper.toUUID(user.getId().toString())).thenReturn(user.getId());
        Mockito.when(userRepository.findById(UUID.randomUUID())).thenThrow(NotFoundException.class);

        Assertions.assertThrows(NotFoundException.class, () -> {
            userService.update(user.getId().toString(), userDto, user.getToken());
        });
    }

    @Test
    public void givenUpdateThatEmailAlreadyExistsThenThrowsConflictException() {

        Mockito.when(userMapper.toUUID(user.getId().toString())).thenReturn(user.getId());
        Mockito.when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        Mockito.when(userMapper.updateEntity(user, userDto)).thenReturn(user);
        Mockito.when(userRepository.save(user)).thenThrow(new DataIntegrityViolationException("unique_email_constraint"));

        Assertions.assertThrows(ConflictException.class, () -> {
            userService.update(user.getId().toString(), userDto, user.getToken());
        });

    }

    @Test
    public void givenUpdateThatFailSavingUserThenThrowsInternalServerErrorException() {

        Mockito.when(userMapper.toUUID(user.getId().toString())).thenReturn(user.getId());
        Mockito.when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        Mockito.when(userMapper.updateEntity(user, userDto)).thenReturn(user);
        Mockito.when(userRepository.save(user)).thenThrow(DataIntegrityViolationException.class);

        Assertions.assertThrows(InternalServerErrorException.class, () -> {
            userService.update(user.getId().toString(), userDto, user.getToken());
        });

    }

    @Test
    public void givenDeleteUserThatValidIdThenDeleteSuccessfully() {

        Mockito.when(userMapper.toUUID(user.getId().toString())).thenReturn(user.getId());
        Mockito.when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        userService.delete(user.getId().toString(), user.getToken());

        verify(userMapper, times(1)).toUUID(user.getId().toString());
        verify(userRepository, times(1)).findById(user.getId());
    }

    @Test
    public void givenDeleteUserThatInvalidIdThenReturnBadRequestException() {

        Mockito.when(userMapper.toUUID(user.getId().toString())).thenThrow(BadRequestException.class);

        Assertions.assertThrows(BadRequestException.class, () -> {
            userService.delete(user.getId().toString(), user.getToken());
        });
    }

    @Test
    public void givenDeleteUserThatNotFoundIdThenReturnNotFoundException() {

        Mockito.when(userMapper.toUUID(user.getId().toString())).thenReturn(user.getId());
        Mockito.when(userRepository.findById(UUID.randomUUID())).thenThrow(NotFoundException.class);

        Assertions.assertThrows(NotFoundException.class, () -> {
            userService.delete(user.getId().toString(), user.getToken());
        });
    }

}
