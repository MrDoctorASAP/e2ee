package com.e2ee.api.controller.dto;

import com.e2ee.api.repository.entities.UserProfile;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.function.Function;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileDto {

    private Long userId;
    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private String status;

    public static Function<UserProfile, UserProfileDto> mapping() {
        return profile -> new UserProfileDto(
                profile.getUser().getId(),
                profile.getUser().getUsername(),
                profile.getFirstName(),
                profile.getLastName(),
                profile.getEmail(),
                profile.getStatus()
        );
    }

}
