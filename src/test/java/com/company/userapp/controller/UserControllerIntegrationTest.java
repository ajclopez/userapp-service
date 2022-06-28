package com.company.userapp.controller;

import com.company.userapp.dto.model.AppUserDto;
import com.company.userapp.dto.model.UserDto;

import com.company.userapp.model.User;
import com.company.userapp.repository.UserRepository;
import com.company.userapp.service.mapper.UserMapper;
import com.company.userapp.util.JWTUtil;
import com.company.userapp.util.UtilTest;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.*;

import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import org.springframework.web.context.WebApplicationContext;

import javax.annotation.Resource;
import java.util.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class UserControllerIntegrationTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserMapper userMapper;

    @Resource
    private FilterChainProxy springSecurityFilterChain;

    private MockMvc mockMvc;

    private UserDto userDto;

    @BeforeEach
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.context).addFilters(this.springSecurityFilterChain).build();
        userDto = UtilTest.buildUserDto();
    }

    private User savingUser(String email) {
        userDto.setEmail(email);
        User _user = userMapper.toUser(userDto);
        return userRepository.save(_user);
    }

    @Test
    public void createNewUserThenReturnCreated() throws Exception {

        MvcResult result = mockMvc.perform(
                        post("/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(new ObjectMapper().writeValueAsString(userDto)))
                .andExpect(status().isCreated())
                .andReturn();

        String id = JsonPath.parse(result.getResponse().getContentAsString()).read("$.id").toString();
        String email = JsonPath.parse(result.getResponse().getContentAsString()).read("$.email").toString();

        Assertions.assertNotNull(id);
        Assertions.assertNotNull(email);
    }

    @Test
    public void createNewUserWithOutPasswordThenReturnBadRequestException() throws Exception {
        userDto.setPassword(null);
        mockMvc.perform(
                        post("/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(new ObjectMapper().writeValueAsString(userDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void createNewUserWithEmailAlreadyExistsThenReturnConflictException() throws Exception {

        userDto.setEmail("testing@domain.cl");
        userRepository.save(UtilTest.buildUser(userDto));

        mockMvc.perform(
                        post("/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(new ObjectMapper().writeValueAsString(userDto)))
                .andExpect(status().isConflict());
    }

    @Test
    public void loginUserNewThenReturnOk() throws Exception {

        User _user = savingUser("testing_login@domain.cl");
        AppUserDto login = new AppUserDto(_user.getEmail(), userDto.getPassword());

        MvcResult resultLogin = mockMvc.perform(
                post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(login)))
                .andExpect(status().isOk())
                .andReturn();

        Assertions.assertNotNull(resultLogin.getResponse().getHeader(JWTUtil.HEADER_AUTHORIZATION));
    }

    @Test
    public void loginWithOutEmailThenReturnStatusUnauthorized() throws Exception {

        User _user = savingUser("testing_login_notemail@domain.cl");
        AppUserDto login = new AppUserDto(null, userDto.getPassword());

        mockMvc.perform(
                        post("/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(new ObjectMapper().writeValueAsString(login)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void loginWithOutPasswordThenReturnStatusUnauthorized() throws Exception {

        User _user = savingUser("testing_login_notpassword@domain.cl");
        AppUserDto login = new AppUserDto(_user.getEmail(), null);

        mockMvc.perform(
                        post("/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(new ObjectMapper().writeValueAsString(login)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void loginWithInvalidEmailAndPasswordThenReturnStatusUnauthorized() throws Exception {

        AppUserDto login = new AppUserDto("fake-user@domain.cl", "fake");

        mockMvc.perform(
                        post("/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(new ObjectMapper().writeValueAsString(login)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void findByIdThenReturnOk() throws Exception {

        User _user = savingUser("testing_findbyid@domain.cl");

        MvcResult result = mockMvc.perform(
                        get("/users/{id}", _user.getId().toString())
                                .header(HttpHeaders.AUTHORIZATION, String.format("Bearer %s", _user.getToken()))
                                .contentType(MediaType.APPLICATION_JSON))

                .andExpect(status().isOk())
                .andReturn();

        String id = JsonPath.parse(result.getResponse().getContentAsString()).read("$.id").toString();
        String email = JsonPath.parse(result.getResponse().getContentAsString()).read("$.email").toString();

        Assertions.assertNotNull(id);
        Assertions.assertEquals(_user.getId().toString(), id);
        Assertions.assertNotNull(email);
        Assertions.assertEquals(userDto.getEmail(), email);

    }

    @Test
    public void findByIdThenReturnNotFoundException() throws Exception {

        User _user = savingUser("testing_findbyid_notfound@domain.cl");

        mockMvc.perform(
                        get("/users/{id}", UUID.randomUUID().toString())
                                .header(HttpHeaders.AUTHORIZATION, String.format("Bearer %s", _user.getToken()))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void findByIdThenReturnBadRequestException() throws Exception {

        User _user = savingUser("testing_findbyid_badrequest@domain.cl");

        mockMvc.perform(
                        get("/users/{id}", UUID.randomUUID().toString() + "-invalid")
                                .header(HttpHeaders.AUTHORIZATION, String.format("Bearer %s", _user.getToken()))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void updateThenReturnOk() throws Exception {

        User _user = savingUser("testing_updated@domain.cl");

        userDto.setEmail("testing_newupdated@domain.cl");

        MvcResult result = mockMvc.perform(
                        put("/users/{id}", _user.getId().toString())
                                .contentType(MediaType.APPLICATION_JSON)
                                .header(JWTUtil.HEADER_AUTHORIZATION, String.format("Bearer %s", _user.getToken()))
                                .content(new ObjectMapper().writeValueAsString(userDto)))
                .andExpect(status().isOk())
                .andReturn();

        String id = JsonPath.parse(result.getResponse().getContentAsString()).read("$.id").toString();
        String email = JsonPath.parse(result.getResponse().getContentAsString()).read("$.email").toString();

        Assertions.assertNotNull(id);
        Assertions.assertEquals(_user.getId().toString(), id);
        Assertions.assertNotNull(email);
        Assertions.assertEquals(userDto.getEmail(), email);

    }

    @Test
    public void updateThenReturnNotFoundException() throws Exception {

        User _user = savingUser("testing_updated_notfound@domain.cl");

        mockMvc.perform(
                        put("/users/{id}", UUID.randomUUID().toString())
                                .contentType(MediaType.APPLICATION_JSON)
                                .header(JWTUtil.HEADER_AUTHORIZATION, String.format("Bearer %s", _user.getToken()))
                                .content(new ObjectMapper().writeValueAsString(userDto)))
                .andExpect(status().isNotFound());
    }

    @Test
    public void deleteThenReturnNotContent() throws Exception {

        User _user = savingUser("testing_delete@domain.cl");
        mockMvc.perform(
                        delete("/users/{id}", _user.getId().toString())
                                .header(JWTUtil.HEADER_AUTHORIZATION, String.format("Bearer %s", _user.getToken()))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    public void deleteThenReturnNotFoundException() throws Exception {

        User _user = savingUser("testing_delete_notfound@domain.cl");

        mockMvc.perform(
                        delete("/users/{id}", UUID.randomUUID().toString())
                                .header(JWTUtil.HEADER_AUTHORIZATION, String.format("Bearer %s", _user.getToken()))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void deleteThenReturnBadRequestException() throws Exception {

        User _user = savingUser("testing_delete_badrequest@domain.cl");

        mockMvc.perform(
                        delete("/users/{id}", UUID.randomUUID().toString() + "-invalid")
                                .header(JWTUtil.HEADER_AUTHORIZATION, String.format("Bearer %s", _user.getToken()))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

}
