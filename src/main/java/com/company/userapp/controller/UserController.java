package com.company.userapp.controller;

import com.company.userapp.dto.model.UserDto;
import com.company.userapp.dto.response.UserResponse;
import com.company.userapp.service.UserService;
import com.company.userapp.util.JWTUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import javax.validation.constraints.NotNull;

@RestController
@RequestMapping("/users")
public class UserController implements UserControllerApi {

    private UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Override
    @PostMapping
    public ResponseEntity<UserResponse> create(UserDto userDto) {

        return ResponseEntity.status(HttpStatus.CREATED).body(userService.create(userDto));
    }

    @Override
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> findById(@Valid @NotNull(message = "Id must not null") String id, HttpServletRequest request) {

        return ResponseEntity.ok(userService.findById(id, request.getHeader(JWTUtil.HEADER_AUTHORIZATION)));
    }

    @Override
    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> update(@Valid @NotNull(message = "Id must not null") String id, UserDto userDto, HttpServletRequest request) {

        return ResponseEntity.ok(userService.update(id, userDto, request.getHeader(JWTUtil.HEADER_AUTHORIZATION)));
    }

    @Override
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@Valid @NotNull(message = "Id must not null") String id, HttpServletRequest request) {

        userService.delete(id, request.getHeader(JWTUtil.HEADER_AUTHORIZATION));
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

}
