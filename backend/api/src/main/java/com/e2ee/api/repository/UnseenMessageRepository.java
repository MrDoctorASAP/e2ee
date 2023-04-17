package com.e2ee.api.repository;

import com.e2ee.api.controller.dto.UnseenChatDto;
import com.e2ee.api.repository.entities.UnseenMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UnseenMessageRepository extends JpaRepository<UnseenMessage, Long> {

    List<UnseenMessage> findAllByUserId(Long userId);
    void deleteAllByUserIdAndChatId(Long userId, Long chatId);

//    @Query("""
//            SELECT UNSEEN.CHAT_ID, COUNT(*) AS COUNT FROM UNSEEN
//                WHERE UNSEEN.USER_ID = $1
//                GROUP BY UNSEEN.CHAT_ID
//            """)
//    List<UnseenChatDto> getUnseenChatsByUserId(Long userId);

}
