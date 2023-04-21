package com.e2ee.api.service;

import com.e2ee.api.controller.dto.FlatUnseenChat;
import com.e2ee.api.repository.UnseenMessageRepository;
import com.e2ee.api.repository.entities.ChatMember;
import com.e2ee.api.repository.entities.Message;
import com.e2ee.api.repository.entities.UnseenMessage;
import com.e2ee.api.repository.entities.User;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import static java.util.function.Predicate.not;

@Service
@AllArgsConstructor
public class UnseenMessageService {

    private final UnseenMessageRepository unseenRepository;

    private final ChatService chatService;
    private final UserService userService;

    public List<FlatUnseenChat> getUnseenChats(User user) {
        userService.checkUserExists(user);
        return unseenRepository.getUnseenChatsByUserId(user.getId());
    }

    public void seen(User user, Long chatId) {
        userService.checkUserExists(user);
        chatService.checkChatExistsById(chatId);
        unseenRepository.deleteAllByUserIdAndChatId(user.getId(), chatId);
    }

    void publish(User user, Message message) {
        List<ChatMember> members = chatService.getChatMembers(user, message.getChatId());
        List<UnseenMessage> unseenMessages = members.stream()
                .map(ChatMember::getUserId)
                .filter(not(message.getUserId()::equals))
                .map(UnseenMessage.mapping(message))
                .toList();
        unseenRepository.saveAll(unseenMessages);
    }

}
