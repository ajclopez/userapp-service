package com.company.userapp.dto.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PhoneDto {

    @Schema(description = "Phone number", required = true, example = "1234567")
    @NotEmpty(message = "number must not be null nor empty")
    private String number;

    @Schema(description = "City code", required = true, example = "1")
    @NotEmpty(message = "citycode must not be null nor empty")
    @JsonProperty("citycode")
    private String cityCode;

    @Schema(description = "Contry code", required = true, example = "57")
    @NotEmpty(message = "contrycode must not be null nor empty")
    @JsonProperty("contrycode")
    private String countryCode;

}
