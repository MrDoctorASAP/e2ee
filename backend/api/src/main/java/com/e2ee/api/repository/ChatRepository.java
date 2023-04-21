package com.e2ee.api.repository;

import com.e2ee.api.repository.entities.Chat;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatRepository extends JpaRepository<Chat, Long> {

    @Query(value = """
            SELECT CHAT.* FROM CHAT_MEMBER
                LEFT JOIN CHAT ON CHAT.ID = CHAT_MEMBER.CHAT_ID
                WHERE CHAT_MEMBER.USER_ID = ?1
            """, nativeQuery = true)
    List<Chat> findAllByUserId(Long userId);

}
