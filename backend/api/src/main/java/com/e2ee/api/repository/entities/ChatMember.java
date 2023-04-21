package com.e2ee.api.repository.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.function.Function;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long chatId;
    private Long userId;

    public ChatMember(Long chatId, Long userId) {
        this.chatId = chatId;
        this.userId = userId;
    }

    public static Function<Long, ChatMember> mapping(Long chatId) {
        return userId -> ChatMember.builder()
                .chatId(chatId)
                .userId(userId)
                .build();
    }
}
