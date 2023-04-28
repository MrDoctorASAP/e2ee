package com.e2ee.api.repository;

import com.e2ee.api.repository.entities.secure.SecureChatAccept;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SecureChatAcceptRepository extends JpaRepository<SecureChatAccept, Long> {
    Optional<SecureChatAccept> findBySecureChatId(String secureChatId);
    List<SecureChatAccept> findAllBySecureChatIdIn(Iterable<String> secureChatIds);
    void deleteBySecureChatId(String secureChatId);
}
