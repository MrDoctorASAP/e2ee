package com.e2ee.api.repository;

import com.e2ee.api.repository.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    boolean existsAllByIdIn(Iterable<Long> ids);
    List<User> findAllByIdIn(Iterable<Long> ids);
}
