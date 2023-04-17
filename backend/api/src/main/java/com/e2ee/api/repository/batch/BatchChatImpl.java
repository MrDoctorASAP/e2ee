package com.e2ee.api.repository.batch;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BatchChatImpl {

    private long chatId;
    private boolean personal;

    private long unseen;

    private Long messageId;
    private Long messageDate;
    private String messageText;

    private Long senderId;
    private String senderUsername;
    private String senderFirstName;
    private String senderLastName;

    private Long personalId;
    private String personalUsername;
    private String personalFirstName;
    private String personalLastName;

    private String groupName;

}
