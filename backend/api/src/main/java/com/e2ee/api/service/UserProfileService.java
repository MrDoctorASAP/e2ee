package com.e2ee.api.service;

import com.e2ee.api.controller.dto.UserDto;
import com.e2ee.api.controller.dto.UserProfileDto;
import com.e2ee.api.repository.UserProfileRepository;
import com.e2ee.api.repository.entities.User;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class UserProfileService {

    private final UserProfileRepository userProfileRepository;

    public List<UserDto> getUserProfiles(List<Long> ids) {
        return userProfileRepository.findAllById(ids)
                .stream()
                .map(UserDto::fromEntity)
                .toList();
    }

    public UserDto updateProfile(User user, UserProfileDto userProfileDto) {
        return null;
    }

}
