package com.e2ee.api.controller.dto;

import com.e2ee.api.repository.entities.UserProfile;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {

    private Long userId;
    private String username;
    private String firstName;
    private String lastName;

    public static UserDto fromEntity(UserProfile userProfile) {
        return new UserDto(
                userProfile.getUser().getId(),
                userProfile.getUser().getUsername(),
                userProfile.getFirstName(),
                userProfile.getLastName()
        );
    }
}
