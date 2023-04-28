package com.e2ee.api.repository;

import com.e2ee.api.controller.dto.secure.SecureChatIdDto;
import com.e2ee.api.repository.entities.secure.SecureChatInvite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SecureChatInviteRepository extends JpaRepository<SecureChatInvite, Long> {
    List<SecureChatInvite> findAllByRecipientId(Long recipientId);

    void deleteBySecureChatId(String secureChatId);
}
