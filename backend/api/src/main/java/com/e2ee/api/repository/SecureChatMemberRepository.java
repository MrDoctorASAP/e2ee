package com.e2ee.api.repository;

import com.e2ee.api.repository.entities.secure.SecureChatMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SecureChatMemberRepository extends JpaRepository<SecureChatMember, Long> {
    List<SecureChatMember> findAllByUserId(Long userId);
    List<SecureChatMember> findAllBySecureChatId(String secureChatId);
}
