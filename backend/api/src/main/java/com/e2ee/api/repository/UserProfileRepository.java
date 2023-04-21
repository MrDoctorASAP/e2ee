package com.e2ee.api.repository;

import com.e2ee.api.repository.entities.UserProfile;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {
    List<UserProfile> findAllByUserIdIn(Iterable<Long> userIds);
    Optional<UserProfile> findByUserId(Long userId);
}
