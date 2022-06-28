package com.company.userapp.service.mapper;

import com.company.userapp.dto.model.UserDto;
import com.company.userapp.dto.response.UserResponse;
import com.company.userapp.exception.BadRequestException;

import com.company.userapp.model.User;

import com.company.userapp.util.UtilTest;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.core.env.Environment;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.*;

import static org.mockito.ArgumentMatchers.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class UserMapperTest {

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @Mock
    private Environment environment;

    @InjectMocks
    private UserMapper userMapper;

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
    public void givenUserDtoConvertToUser() {

        Mockito.when(objectMapper.convertValue(userDto, User.class)).thenReturn(user);
        Mockito.when(passwordEncoder.encode(user.getPassword())).thenReturn("x-password-x");
        Mockito.when(environment.getProperty("user.secret.token", "SECRET_TOKEN_DEMO")).thenReturn("SECRET_TOKEN_DEMO");
        User user = userMapper.toUser(userDto);

        Assertions.assertNotNull(user);
        Assertions.assertNotNull(user.getCreated());
        Assertions.assertNotNull(user.getModified());
        Assertions.assertNotNull(user.getToken());
        Assertions.assertEquals("x-password-x", user.getPassword());
    }

    @Test
    public void givenUserAndUserDto() {

        Mockito.when(objectMapper.convertValue(anySet(), any(TypeReference.class))).thenReturn(user.getPhones());

        userDto.setName("New name");
        User _user = userMapper.updateEntity(user, userDto);

        Assertions.assertEquals(user.getName(), _user.getName());
    }

    @Test
    public void givenEntityToUserResponse() {

        Mockito.when(objectMapper.convertValue(any(), any(Class.class))).thenReturn(userResponse);

        UserResponse response = userMapper.entityToUserResponse(user);

        Assertions.assertEquals(response.getId(), userResponse.getId());
    }


    @Test
    public void givenStringConvertToUUID() {

        String id = UUID.randomUUID().toString();
        UUID uuid = userMapper.toUUID(id);

        Assertions.assertEquals(UUID.fromString(id), uuid);
    }

    @Test
    public void givenStringWithInvalidFormatConvertToUUIDThenReturnBadRequestException() {

        Assertions.assertThrows(BadRequestException.class, () -> {
            userMapper.toUUID(UUID.randomUUID().toString() + "invalid");
        });

    }

}
