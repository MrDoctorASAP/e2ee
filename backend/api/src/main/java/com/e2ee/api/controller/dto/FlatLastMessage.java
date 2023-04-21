package com.e2ee.api.controller.dto;

public interface FlatLastMessage {
    Long getChatId();
    Long getSenderId();
    Long getMessageId();
    Long getMessageDate();
    String getMessageText();
}
