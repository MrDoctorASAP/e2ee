package com.e2ee.api.service;

import com.e2ee.api.controller.dto.FlatLastMessage;
import com.e2ee.api.controller.dto.MessageDto;
import com.e2ee.api.repository.MessageRepository;
import com.e2ee.api.repository.entities.Message;
import com.e2ee.api.repository.entities.User;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class MessageService {

    private UserService userService;
    private ChatService chatService;
    private UnseenMessageService unseenService;
    private MessagingService messagingService;

    private MessageRepository messageRepository;

    @Transactional
    public Message sendMessage(User user, MessageDto messageDto) {
        Long userId = user.getId();
        Long chatId = messageDto.getChatId();
        userService.checkUserExists(user);
        chatService.checkChatExistsById(chatId);
        chatService.checkIsChatMember(userId, chatId);
        Message message = messageRepository.save(messageDto.toEntity(userId));
        unseenService.publish(user, message);
        messagingService.publish(message);
        log.info("Message sent: {}", message);
        return message;
    }

    public List<Message> getMessages(User user, Long chatId, int page, int count) {
        Long userId = user.getId();
        userService.checkUserExists(user);
        chatService.checkChatExistsById(chatId);
        chatService.checkIsChatMember(userId, chatId);
        Pageable pageable = PageRequest.of(page, count, Sort.by("date"));
        return messageRepository.findAllByChatId(chatId, pageable);
    }

    public List<FlatLastMessage> getLastMessages(User user, int page, int count) {
        return messageRepository.findLastMessagesByUserId(user.getId());
    }

}
