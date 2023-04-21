package com.e2ee.api.repository;

import com.e2ee.api.controller.dto.FlatUnseenChat;
import com.e2ee.api.repository.entities.UnseenMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UnseenMessageRepository extends JpaRepository<UnseenMessage, Long> {

    void deleteAllByUserIdAndChatId(Long userId, Long chatId);

    @Query(value = """
            SELECT UNSEEN_MESSAGE.CHAT_ID AS chatId, COUNT(*) AS count FROM UNSEEN_MESSAGE
                WHERE UNSEEN_MESSAGE.USER_ID = ?1
                GROUP BY UNSEEN_MESSAGE.CHAT_ID
            """, nativeQuery = true)
    List<FlatUnseenChat> getUnseenChatsByUserId(Long userId);

}
