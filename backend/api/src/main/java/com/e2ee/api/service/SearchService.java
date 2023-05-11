package com.e2ee.api.service;

import com.e2ee.api.controller.dto.UserDto;
import com.e2ee.api.repository.UserProfileRepository;
import com.e2ee.api.repository.UserRepository;
import com.e2ee.api.repository.entities.User;
import com.e2ee.api.repository.entities.UserProfile;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;

@Slf4j
@Service
@RequiredArgsConstructor
public class SearchService {

    private final UserProfileRepository profileRepository;
    private final UserRepository userRepository;

    public List<UserDto> search(String query) {
        query = query.trim().toLowerCase(Locale.ROOT);
        if (query.startsWith("@")) {
            return searchByUsername(query.substring(1));
        }
        return searchByName(query);
    }

    public List<UserDto> searchByUsername(String query) {
        if (query.isEmpty()) return List.of();
        List<Long> ids = userRepository.findAll()
                .stream()
                .filter(user -> user.getUsername().toLowerCase().contains(query))
                .map(User::getId)
                .toList();
        return profileRepository.findAllByUserIdIn(ids)
                .stream()
                .map(UserDto.mapping())
                .toList();
    }

    public List<UserDto> searchByName(String query) {
        return profileRepository.findAll()
                .stream()
                .filter(profile -> profile.getFirstName().toLowerCase().contains(query) ||
                        profile.getLastName().toLowerCase().contains(query))
                .map(UserDto.mapping())
                .toList();
    }

}
