package com.e2ee.api.service;

import com.e2ee.api.controller.dto.UserDto;
import com.e2ee.api.controller.dto.UserProfileDto;
import com.e2ee.api.repository.UserProfileRepository;
import com.e2ee.api.repository.entities.User;
import com.e2ee.api.repository.entities.UserProfile;
import com.e2ee.api.service.exceptons.ServiceException;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@AllArgsConstructor
public class UserProfileService {

    private final UserProfileRepository userProfileRepository;

    public UserProfileDto getUserProfile(Long userId) {
        return userProfileRepository.findByUserId(userId)
                .map(UserProfileDto.mapping())
                .orElseThrow(() -> new ServiceException("User not found"));
    }

    public List<UserDto> getUsers(Collection<Long> userIds) {
        // TODO: Check not found users
        Set<Long> uniqueIds = new HashSet<>(userIds);
        return userProfileRepository.findAllByUserIdIn(uniqueIds)
                .stream()
                .map(UserDto.mapping())
                .toList();
    }
}
