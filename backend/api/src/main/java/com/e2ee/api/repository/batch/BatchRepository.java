package com.e2ee.api.repository.batch;

import com.e2ee.api.repository.entities.Chat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BatchRepository extends JpaRepository<Chat, Long> {

    @Query(value = """
            SELECT
                CHAT.ID AS chatId,
                CHAT.PERSONAL as personal,
                CASEWHEN(UNSEEN IS NULL, 0, UNSEEN) AS unseen,
                USER_DETAILS.ID AS senderId,
                USER_DETAILS.USERNAME AS senderUsername,
                USER_PROFILE.FIRST_NAME AS senderFirstName,
                USER_PROFILE.LAST_NAME AS senderLastName,
                UD.ID AS personalId,
                UD.USERNAME AS personalUsername,
                UP.FIRST_NAME AS personalFirstName,
                UP.LAST_NAME AS personalLastName,
                GROUP_CHAT_INFO.NAME AS groupName,
                MESSAGE.ID AS messageId,
                MESSAGE.DATE AS messageDate,
                MESSAGE.MESSAGE AS messageText,
                GROUP_CHAT_INFO.OWNER_ID AS ownerId
            FROM (
                SELECT MAX(MESSAGE.ID) AS MAX FROM MESSAGE
                    LEFT JOIN CHAT_MEMBER ON CHAT_MEMBER.CHAT_ID = MESSAGE.CHAT_ID WHERE CHAT_MEMBER.USER_ID = ?1
                        GROUP BY MESSAGE.CHAT_ID ) AS M
                LEFT JOIN MESSAGE ON MESSAGE.ID = M.MAX
                RIGHT JOIN CHAT ON MESSAGE.CHAT_ID = CHAT.ID
                RIGHT JOIN CHAT_MEMBER ON CHAT_MEMBER.CHAT_ID = CHAT.ID
                LEFT JOIN GROUP_CHAT_INFO ON CHAT.ID = GROUP_CHAT_INFO.CHAT_ID
                LEFT JOIN USER_DETAILS ON MESSAGE.USER_ID = USER_DETAILS.ID
                LEFT JOIN USER_PROFILE ON MESSAGE.USER_ID = USER_PROFILE.USER_ID
                LEFT JOIN (
                    SELECT CHAT_ID AS UNSEEN_CHAT_ID, COUNT(*) AS UNSEEN
                        FROM UNSEEN_MESSAGE
                        WHERE USER_ID = ?1
                        GROUP BY CHAT_ID
                    ) ON UNSEEN_CHAT_ID = CHAT.ID
                LEFT JOIN CHAT_MEMBER AS CM ON CM.CHAT_ID = CHAT.ID AND CHAT.PERSONAL AND CM.USER_ID != ?1
                LEFT JOIN USER_DETAILS AS UD ON CM.USER_ID = UD.ID
                LEFT JOIN USER_PROFILE AS UP ON CM.USER_ID = UP.USER_ID
                WHERE CHAT_MEMBER.USER_ID = ?1
            """, nativeQuery = true)
    List<FlatBatchChat> findAllByUserId(Long userId);

}
