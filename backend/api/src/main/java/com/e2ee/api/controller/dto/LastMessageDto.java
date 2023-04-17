package com.e2ee.api.controller.dto;

import com.e2ee.api.repository.entities.Message;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LastMessageDto {
    private Long chatId;
    private Message message;
}
