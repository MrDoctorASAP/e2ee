package com.e2ee.api.controller.dto;

import com.e2ee.api.repository.entities.Message;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.function.Function;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShortMessage {

    private Long messageId;
    private Long senderId;
    private String text;
    private Long date;

    public static Function<Message, ShortMessage> mapping() {
        return message -> new ShortMessage(message.getId(), message.getUserId(), message.getMessage(), message.getDate());
    }
}
