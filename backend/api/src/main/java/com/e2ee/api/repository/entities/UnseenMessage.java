package com.e2ee.api.repository.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import java.util.function.Function;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UnseenMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long chatId;
    private Long userId;
    private Long messageId;

    public static Function<Long, UnseenMessage> mapping(Message message) {
        return receiverId -> UnseenMessage.builder()
                .messageId(message.getId())
                .chatId(message.getChatId())
                .userId(receiverId)
                .build();
    }

}
