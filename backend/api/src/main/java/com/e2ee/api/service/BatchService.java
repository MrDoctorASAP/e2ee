package com.e2ee.api.service;

import com.e2ee.api.controller.BatchController;
import com.e2ee.api.controller.dto.BatchChat;
import com.e2ee.api.controller.dto.BatchMessages;
import com.e2ee.api.controller.dto.ShortMessage;
import com.e2ee.api.controller.dto.UserDto;
import com.e2ee.api.repository.batch.BatchRepository;
import com.e2ee.api.repository.batch.FlatBatchChat;
import com.e2ee.api.repository.entities.*;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class BatchService {

    private final ChatService chatService;
    private final MessageService messageService;
    private final UserProfileService profileService;

    private final BatchRepository batchRepository;

    public BatchMessages getMessages(User user, Long chatId, int page, int count) {
        chatService.checkIsChatMember(user.getId(), chatId);
        List<UserDto> members = profileService.getUsers(
                chatService.getChatMembers(chatId)
                        .stream()
                        .map(ChatMember::getUserId)
                        .toList());
        List<ShortMessage> messages = messageService.getMessages(user, chatId, page, count)
                .stream()
                .map(ShortMessage.mapping())
                .toList();
        return new BatchMessages(members, messages);
    }

    public List<BatchChat> getChats(User user) {
        return batchRepository.findAllByUserId(user.getId())
                .stream()
                .map(BatchChat.mapping())
                .toList();
    }
}
