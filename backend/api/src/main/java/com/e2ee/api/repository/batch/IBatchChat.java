package com.e2ee.api.repository.batch;

public interface IBatchChat {
    Long getChatId();
    Boolean getPersonal();
    Long getUnseen();
    Long getMessageId();
    Long getMessageDate();
    String getMessageText();
    Long getSenderId();
    String getSenderUsername();
    String getSenderFirstName();
    String getSenderLastName();
    Long getPersonalId();
    String getPersonalUsername();
    String getPersonalFirstName();
    String getPersonalLastName();
    String getGroupName();
}
