package com.e2ee.api.repository;

import com.e2ee.api.repository.entities.ChatMember;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatMemberRepository extends JpaRepository<ChatMember, Long> {

    boolean existsByChatIdAndUserId(Long chatId, Long userId);

    List<ChatMember> findAllByUserId(Long userId, Pageable pageable);
    List<ChatMember> findAllByUserId(Long userId);

    List<ChatMember> findAllByChatId(Long chatId, Pageable pageable);
    List<ChatMember> findAllByChatId(Long chatId);

}
