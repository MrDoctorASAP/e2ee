package com.e2ee.api.controller.dto;

import com.e2ee.api.repository.batch.FlatBatchChat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.function.Function;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BatchChat {

    private ChatDetails details;
    private GroupChatDetails group;
    private PersonalChatDetails personal;
    private LastMessage last;

    public static Function<FlatBatchChat, BatchChat> mapping() {
        return flat -> {
            GroupChatDetails group = !flat.getPersonal() ?
                    new GroupChatDetails(flat.getOwnerId(), flat.getGroupName()) : null;
            PersonalChatDetails personal = flat.getPersonal() ? new PersonalChatDetails(new UserDto(
                    flat.getPersonalId(),
                    flat.getPersonalUsername(),
                    flat.getPersonalFirstName(),
                    flat.getPersonalLastName())) : null;
            LastMessage lastMessage = flat.getMessageId() != null ? new LastMessage(
                    new ShortMessage(
                            flat.getMessageId(), flat.getSenderId(),
                            flat.getMessageText(), flat.getMessageDate()),
                    new UserDto(
                            flat.getSenderId(), flat.getSenderUsername(),
                            flat.getSenderFirstName(), flat.getSenderLastName())) : null;
            return new BatchChat(new ChatDetails(flat.getChatId(), flat.getPersonal(), flat.getUnseen()),
                    group, personal, lastMessage);
        };
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ChatDetails {
        private long chatId;
        private boolean personal;
        private long unseen;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PersonalChatDetails {
        private UserDto recipient;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GroupChatDetails {
        private long ownerId;
        private String chatName;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LastMessage {
        private ShortMessage message;
        private UserDto sender;
    }

}