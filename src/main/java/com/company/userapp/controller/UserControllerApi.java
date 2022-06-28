package com.company.userapp.controller;

import com.company.userapp.dto.model.UserDto;
import com.company.userapp.dto.response.ErrorResponse;
import com.company.userapp.dto.response.UserResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import javax.servlet.http.HttpServletRequest;

import javax.validation.Valid;

@Tag(name = "Users", description = "API allow to manage users")
public interface UserControllerApi {

    /**
     * POST /users Create a new user
     *
     * @return New user (status code 201)
     *         or Malformed syntax of the request params (status code 400)
     *         or the email user already registered (status code 409)
     *         or Server encountered an unexpected problem (status code 500)
     */
    @Operation(summary = "Create a new user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User created successfully", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponse.class)) }),
            @ApiResponse(responseCode = "400", description = "Bad Request", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)) }),
            @ApiResponse(responseCode = "409", description = "Conflict", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)) }),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)) }) })
    public ResponseEntity<UserResponse> create(@io.swagger.v3.oas.annotations.parameters.RequestBody(
            required = true,
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = UserDto.class))) @Valid @RequestBody UserDto userDto);

    /**
     * GET /users/:id Return user by id
     *
     * @return Return user (status code 200)
     *         or Malformed syntax of the request params (status code 400)
     *         or Forbidden request (status code 403)
     *         or the user not found (status code 404)
     *         or Server encountered an unexpected problem (status code 500)
     */
    @Operation(summary = "Return user successfully",
            security = @SecurityRequirement(name = "Auth_Jwt_Token"),
            parameters = @Parameter(name = "id", in = ParameterIn.PATH, description = "The user id", required = true))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Return user successfully", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponse.class)) }),
            @ApiResponse(responseCode = "400", description = "Bad Request", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)) }),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)) }),
            @ApiResponse(responseCode = "404", description = "Not found", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)) }),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)) }) })
    public ResponseEntity<UserResponse> findById(@PathVariable(name = "id") String id, HttpServletRequest request);


    /**
     * PUT /users/:id Update user
     *
     * @return Update user (status code 200)
     *         or Malformed syntax of the request params (status code 400)
     *         or Forbidden request (status code 403)
     *         or the user not found (status code 404)
     *         or Server encountered an unexpected problem (status code 500)
     */
    @Operation(summary = "Update user successfully",
            security = @SecurityRequirement(name = "Auth_Jwt_Token"),
            parameters = @Parameter(name = "id", in = ParameterIn.PATH, description = "The user id", required = true))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Return user successfully", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponse.class)) }),
            @ApiResponse(responseCode = "400", description = "Bad Request", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)) }),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)) }),
            @ApiResponse(responseCode = "404", description = "Not found", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)) }),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)) }) })
    public ResponseEntity<UserResponse> update(@PathVariable(name = "id") String id,
                                       @io.swagger.v3.oas.annotations.parameters.RequestBody(
                                               required = true,
                                               content = @Content(
                                                       mediaType = MediaType.APPLICATION_JSON_VALUE,
                                                       schema = @Schema(implementation = UserDto.class))) @Valid @RequestBody UserDto userDto,
                                               HttpServletRequest request);


    /**
     * DELETE /users/:id Delete an user
     *
     * @return Delete user (status code 204)
     *         or Malformed syntax of the request params (status code 400)
     *         or Forbidden request (status code 403)
     *         or the user not found (status code 404)
     *         or Server encountered an unexpected problem (status code 500)
     */
    @Operation(summary = "Delete user successfully",
            security = @SecurityRequirement(name = "Auth_Jwt_Token"),
            parameters = @Parameter(name = "id", in = ParameterIn.PATH, description = "The user id", required = true))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Return user successfully", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = Void.class)) }),
            @ApiResponse(responseCode = "400", description = "Bad Request", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)) }),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)) }),
            @ApiResponse(responseCode = "404", description = "Not found", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)) }),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)) }) })
    public ResponseEntity<Void> delete(@PathVariable(name = "id") String id, HttpServletRequest request);

}
