package com.e2ee.api.service;

import com.e2ee.api.controller.BatchController;
import com.e2ee.api.repository.batch.BatchRepository;
import com.e2ee.api.repository.batch.FlatBatchChat;
import com.e2ee.api.repository.entities.*;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class BatchService {

    private final UserService userService;
    private final ChatService chatService;
    private final MessageService messageService;
    private final UserProfileService profileService;
    private final UnseenMessageService unseenService;

    private final BatchRepository batchRepository;

    public List<BatchController.BatchMessage> getMessages(User user, Long chatId) {
//        Long userId = user.getId();
//        if (!chatRepository.existsById(chatId)) {
//            throw new RuntimeException("ChatNotFound");
//        }
//        if (!memberRepository.existsByChatIdAndUserId(chatId, userId)) {
//            throw new RuntimeException("NotChatMember");
//        }
//        List<Message> messages = messageRepository.findAllByChatId(chatId);
//        List<Long> userIds = messages.stream().map(Message::getUserId).distinct().toList();
//        List<UserDto> userProfiles = profileService.getUserProfiles(userIds);
//        return messages.stream()
//                .map(message -> new BatchController.BatchMessage(userProfiles.stream()
//                        .filter(profile -> profile.getUserId().equals(message.getUserId()))
//                        .findFirst()
//                        .get(), message))
//                .toList();
        return null;
    }

    public List<FlatBatchChat> getChats(User user) {
        // TODO: Remove users
        return batchRepository.findAllByUserId(user.getId());
    }
}
