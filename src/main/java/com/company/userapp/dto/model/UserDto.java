package com.company.userapp.dto.model;

import com.company.userapp.validation.Email;
import com.company.userapp.validation.Password;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;

import java.util.Set;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {

    @Schema(description = "Username", required = true, example = "Juan Rodriguez")
    @NotEmpty(message = "name must not be null nor empty")
    private String name;

    @Schema(description = "User email address", required = true, example = "juan@rodriguez.org")
    @NotEmpty(message = "email must not be null nor empty")
    @Email(message = "email must have a valid format")
    private String email;

    @Schema(description = "User password", required = true, example = "hunter2")
    @NotEmpty(message = "password must not be null nor empty")
    @Password(message = "password must have a valid format")
    private String password;

    @Schema(description = "Contact information", required = true)
    @Valid
    @NotEmpty(message = "phones must not be null nor empty")
    private Set<PhoneDto> phones;

}
