package com.e2ee.api.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserCredentialsDto {


    @NotNull
    @NotBlank(message = "username required")
    @Size(min = 3, max = 255)
    @Pattern(regexp = "\\w+", message = "username may contains only letters and digits")
    private String username;

    @NotNull
    @NotBlank(message = "password required")
    @Size(min = 3, max = 255)
    private String password;

}
