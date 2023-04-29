package com.e2ee.api.repository;

import com.e2ee.api.repository.entities.secure.SecureChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SecureChatMessageRepository extends JpaRepository<SecureChatMessage, Long> {
    List<SecureChatMessage> findAllBySecureChatId(String secureChatId);
    void deleteAllByIdIn(Iterable<Long> ids);
}
