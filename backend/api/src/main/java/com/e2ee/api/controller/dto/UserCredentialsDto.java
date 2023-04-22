package com.e2ee.api.controller.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


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
