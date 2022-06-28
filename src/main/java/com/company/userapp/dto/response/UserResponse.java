package com.company.userapp.dto.response;

import com.company.userapp.dto.model.PhoneDto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserResponse {

    @Schema(description = "User id", required = true, example = "c24437d9-47ba-4a8b-9b6c-8f3a9d7eed58")
    private String id;

    @Schema(description = "Username", required = true, example = "Juan Rodriguez")
    private String name;

    @Schema(description = "User email address", required = true, example = "juan@rodriguez.org")
    private String email;

    @Schema(description = "Contact information", required = true)
    private Set<PhoneDto> phones;

    @Schema(description = "User creation date", format = "yyyy-MM-ddTHH:mm:ss.SSSZ", required = true, example = "2022-06-26T13:37:52.864Z")
    private Instant created;

    @Schema(description = "User modified date", format = "yyyy-MM-ddTHH:mm:ss.SSSZ", required = true, example = "2022-06-26T13:37:52.864Z")
    private Instant modified;

    @Schema(description = "date of last login", format = "yyyy-MM-ddTHH:mm:ss.SSSZ", required = true, example = "2022-06-26T13:37:52.864Z")
    @JsonProperty("last_login")
    private Instant lastLogin;

    @Schema(description = "Identifies if the user is active", required = true, example = "true")
    @JsonProperty("isactive")
    private Boolean isActive;

    @Schema(description = "API access JWT token", required = true, example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c")
    private String token;

}
