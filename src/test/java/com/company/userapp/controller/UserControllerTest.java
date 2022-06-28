package com.company.userapp.controller;

import com.company.userapp.dto.model.UserDto;
import com.company.userapp.dto.response.UserResponse;
import com.company.userapp.exception.BadRequestException;
import com.company.userapp.exception.ConflictException;
import com.company.userapp.exception.InternalServerErrorException;
import com.company.userapp.exception.NotFoundException;
import com.company.userapp.model.User;
import com.company.userapp.service.UserService;
import com.company.userapp.util.JWTUtil;
import com.company.userapp.util.UtilTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import org.springframework.web.context.WebApplicationContext;

import java.util.Arrays;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class UserControllerTest {

    @Autowired
    private WebApplicationContext context;

    @MockBean
    private UserService userService;

    private MockMvc mockMvc;

    private UserDto userDto;

    private UserResponse userResponse;

    private String id;

    @BeforeEach
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.context).build();

        id = UUID.randomUUID().toString();
        userDto = UtilTest.buildUserDto();
        User user = UtilTest.buildUser(userDto);
        userResponse = UtilTest.buildUserResponse(user, userDto.getPhones());
        userResponse.setId(id);

    }

    @Test
    public void whenCreateNewUserThenReturnCreated() throws Exception {

        Mockito.when(userService.create(userDto)).thenReturn(userResponse);

        MvcResult result = mockMvc.perform(
                        post("/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(new ObjectMapper().writeValueAsString(userDto)))
                .andExpect(status().isCreated())
                .andReturn();

        String id = JsonPath.parse(result.getResponse().getContentAsString()).read("$.id").toString();
        String email = JsonPath.parse(result.getResponse().getContentAsString()).read("$.email").toString();

        Assertions.assertEquals(userResponse.getId(), id);
        Assertions.assertEquals(userResponse.getEmail(), email);
    }

    @Test
    public void whenCreatedUserWithInvalidEmailThenThrowsBadRequestException() throws Exception {

        Mockito.when(userService.create(userDto)).thenThrow(BadRequestException.class);

        mockMvc.perform(
                        post("/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(new ObjectMapper().writeValueAsString(userDto)))
                .andExpect(status().isBadRequest());

    }

    @Test
    public void whenCreatedUserWithAlreadyEmailThenThrowsConflictException() throws Exception {
        Mockito.when(userService.create(userDto)).thenThrow(ConflictException.class);

        mockMvc.perform(
                        post("/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(new ObjectMapper().writeValueAsString(userDto)))
                .andExpect(status().isConflict());
    }

    @Test
    public void whenCreatedUserWhenInternalErrorThenThrowsInternalServerErrorException() throws Exception {
        Mockito.when(userService.create(userDto)).thenThrow(InternalServerErrorException.class);

        mockMvc.perform(
                        post("/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(new ObjectMapper().writeValueAsString(userDto)))
                .andExpect(status().isInternalServerError());
    }

    @Test
    public void whenFindByIdThenReturnOk() throws Exception {
        Mockito.when(userService.findById(id, userResponse.getToken())).thenReturn(userResponse);

        MvcResult result = mockMvc.perform(
                        get("/users/{id}", id)
                                .header(HttpHeaders.AUTHORIZATION, userResponse.getToken())
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String id = JsonPath.parse(result.getResponse().getContentAsString()).read("$.id").toString();
        String email = JsonPath.parse(result.getResponse().getContentAsString()).read("$.email").toString();

        Assertions.assertEquals(userResponse.getId(), id);
        Assertions.assertEquals(userResponse.getEmail(), email);
    }

    @Test
    public void whenFindByIdThenThrowsBadRequestException() throws Exception {

        String id = String.format("%s-invalid", UUID.randomUUID().toString());
        Mockito.when(userService.findById(id, userResponse.getToken())).thenThrow(BadRequestException.class);

        mockMvc.perform(
                        get("/users/{id}", id)
                                .header(HttpHeaders.AUTHORIZATION, userResponse.getToken())
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void whenFindByIdThenThrowsNotFoundException() throws Exception {

        String id = UUID.randomUUID().toString();
        Mockito.when(userService.findById(id, userResponse.getToken())).thenThrow(NotFoundException.class);

        mockMvc.perform(
                        get("/users/{id}", id)
                                .header(HttpHeaders.AUTHORIZATION, userResponse.getToken())
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNotFound());
    }

    @Test
    public void whenFindByIdThenThrowsInternalServerErrorException() throws Exception {

        Mockito.when(userService.findById(id, userResponse.getToken())).thenThrow(InternalServerErrorException.class);

        mockMvc.perform(
                        get("/users/{id}", id)
                                .header(HttpHeaders.AUTHORIZATION, userResponse.getToken())
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
    }

    @Test
    public void whenUpdateUserThenReturnOk() throws Exception {

        userDto.setName("User Edit");
        userResponse.setName(userDto.getName());

        Mockito.when(userService.update(id, userDto, userResponse.getToken())).thenReturn(userResponse);

        MvcResult result = mockMvc.perform(
                        put("/users/{id}", id)
                                .contentType(MediaType.APPLICATION_JSON)
                                .header(JWTUtil.HEADER_AUTHORIZATION, userResponse.getToken())
                                .content(new ObjectMapper().writeValueAsString(userDto)))
                .andExpect(status().isOk())
                .andReturn();

        String id = JsonPath.parse(result.getResponse().getContentAsString()).read("$.id").toString();
        String email = JsonPath.parse(result.getResponse().getContentAsString()).read("$.email").toString();
        String name = JsonPath.parse(result.getResponse().getContentAsString()).read("$.name").toString();

        Assertions.assertEquals(userResponse.getId(), id);
        Assertions.assertEquals(userResponse.getEmail(), email);
        Assertions.assertEquals(userResponse.getName(), name);
    }

    @Test
    public void whenUpdateUserThenReturnNotFoundException() throws Exception {

        Mockito.when(userService.update(id, userDto, userResponse.getToken())).thenThrow(NotFoundException.class);

        mockMvc.perform(
                        put("/users/{id}", id)
                                .contentType(MediaType.APPLICATION_JSON)
                                .header(JWTUtil.HEADER_AUTHORIZATION, userResponse.getToken())
                                .content(new ObjectMapper().writeValueAsString(userDto)))
                .andExpect(status().isNotFound());
    }

    @Test
    public void whenUpdateUserThenReturnInternalServerErrorException() throws Exception {

        Mockito.when(userService.update(id, userDto, userResponse.getToken())).thenThrow(InternalServerErrorException.class);

        mockMvc.perform(
                        put("/users/{id}", id)
                                .contentType(MediaType.APPLICATION_JSON)
                                .header(JWTUtil.HEADER_AUTHORIZATION, userResponse.getToken())
                                .content(new ObjectMapper().writeValueAsString(userDto)))
                .andExpect(status().isInternalServerError());
    }

    @Test
    public void whenDeleteUserThenReturnNotContent() throws Exception {
        Mockito.doNothing().when(userService).delete(id, userResponse.getToken());

        mockMvc.perform(
                        delete("/users/{id}", id)
                                .header(JWTUtil.HEADER_AUTHORIZATION, userResponse.getToken())
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    public void whenDeleteUserThenReturnNotFoundException() throws Exception {
        Mockito.doThrow(NotFoundException.class).doNothing().when(userService).delete(id, userResponse.getToken());

        mockMvc.perform(
                        delete("/users/{id}", id)
                                .header(JWTUtil.HEADER_AUTHORIZATION, userResponse.getToken())
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void whenDeleteUserThenReturnInternalServerErrorException() throws Exception {
        Mockito.doThrow(InternalServerErrorException.class).doNothing().when(userService).delete(id, userResponse.getToken());

        mockMvc.perform(
                        delete("/users/{id}", id)
                                .header(JWTUtil.HEADER_AUTHORIZATION, userResponse.getToken())
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
    }

}
