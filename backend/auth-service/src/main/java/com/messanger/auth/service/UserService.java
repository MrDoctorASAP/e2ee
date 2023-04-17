package com.messanger.auth.service;

import com.messanger.auth.repository.UserRepository;
import com.messanger.auth.repository.entities.User;
import com.messanger.auth.service.exceptons.UserAlreadyExistsException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@Primary
public class UserService implements UserDetailsService {

    private final UserRepository repository;
    private final PasswordEncoder encoder;

    public UserService(UserRepository repository, PasswordEncoder encoder) {
        this.repository = repository;
        this.encoder = encoder;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return repository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Username not found."));
    }

    public void createUser(String username, String password) throws UserAlreadyExistsException {
        if (repository.findByUsername(username).isPresent()) {
            throw new UserAlreadyExistsException();
        }
        User user = new User();
        user.setUsername(username);
        user.setPassword(encoder.encode(password));
        repository.save(user);
        log.info("Created user with username: {}", username);
    }

}
