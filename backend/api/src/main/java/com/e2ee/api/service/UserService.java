package com.e2ee.api.service;

import com.e2ee.api.controller.dto.UserCredentialsDto;
import com.e2ee.api.controller.dto.UserRegistrationDto;
import com.e2ee.api.repository.UserProfileRepository;
import com.e2ee.api.repository.UserRepository;
import com.e2ee.api.repository.entities.User;
import com.e2ee.api.repository.entities.UserProfile;
import com.e2ee.api.service.exceptons.UserAlreadyExistsException;
import com.e2ee.api.service.exceptons.UserException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@Primary
@AllArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final UserProfileRepository profileRepository;
    private final PasswordEncoder encoder;

    @Override
    public User loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Username not found: " + username));
    }

    @Transactional
    public User createUser(UserRegistrationDto details) throws UserAlreadyExistsException {
        if (userRepository.findByUsername(details.getCredentials().getUsername()).isPresent()) {
            throw new UserAlreadyExistsException();
        }
        User user = User.builder()
                .username(details.getCredentials().getUsername())
                .password(encoder.encode(details.getCredentials().getPassword()))
                .build();
        User save = userRepository.save(user);
        UserProfile userProfile = UserProfile.builder()
                .user(save)
                .email(details.getEmail())
                .firstName(details.getFirstName())
                .lastName(details.getLastName())
                .status("")
                .build();
        profileRepository.save(userProfile);
        log.info("Created user: {}", userProfile);
        return save;
    }

    @Deprecated
    @Transactional
    public User createUserIfAbsent(UserCredentialsDto credentials) {
        Optional<User> userOptional = userRepository.findByUsername(credentials.getUsername());
        return userOptional.orElseGet(() -> createUser(new UserRegistrationDto(credentials, "Foo", "Bar", "Buzz")));
    }

    public boolean existsByIds(Iterable<Long> userIds) {
        return userRepository.existsAllByIdIn(userIds);
    }

    public void checkExistsById(Long userId) {
        checkExistsByIds(List.of(userId));
    }

    public void checkExistsByIds(List<Long> userIds) {
        if (!existsByIds(userIds)) {
            throw new UserException("User not found");
        }
    }
}
