package com.e2ee.api.controller.dto;

import com.e2ee.api.repository.entities.Message;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageDto {

    private Long chatId;
    private String message;

    public Message toEntity(Long userId) {
        return Message.builder()
                .message(message)
                .chatId(chatId)
                .userId(userId)
                .date(System.currentTimeMillis())
                .build();
    }

}
