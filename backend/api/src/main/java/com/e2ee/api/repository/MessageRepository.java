package com.e2ee.api.repository;

import com.e2ee.api.controller.dto.FlatLastMessage;
import com.e2ee.api.repository.entities.Message;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    List<Message> findAllByChatId(Long chatId, Pageable pageable);
    List<Message> findAllByChatId(Long chatId);

    @Query(value = """
            SELECT CHAT.ID AS chatId, MESSAGE.USER_ID AS senderId, MESSAGE.ID AS messageId,
                MESSAGE.DATE AS messageDate, MESSAGE.MESSAGE AS messageText
            FROM (SELECT MAX(MESSAGE.ID) AS M, MESSAGE.CHAT_ID FROM MESSAGE GROUP BY MESSAGE.CHAT_ID) AS LAST
                LEFT JOIN MESSAGE ON M = MESSAGE.ID
                LEFT JOIN CHAT ON CHAT.ID = MESSAGE.CHAT_ID
                LEFT JOIN CHAT_MEMBER ON CHAT.ID = CHAT_MEMBER.CHAT_ID
                WHERE CHAT_MEMBER.USER_ID = ?1
            """, nativeQuery = true)
    List<FlatLastMessage> findLastMessagesByUserId(Long userId);

}
