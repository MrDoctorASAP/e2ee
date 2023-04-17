package com.e2ee.api.controller;

import com.e2ee.api.controller.dto.UnseenChatDto;
import com.e2ee.api.controller.dto.UserDto;
import com.e2ee.api.repository.entities.Chat;
import com.e2ee.api.repository.entities.GroupChatInfo;
import com.e2ee.api.repository.entities.Message;
import com.e2ee.api.repository.entities.User;
import com.e2ee.api.service.BatchService;
import com.e2ee.api.service.UserAuthenticationService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/batch")
public class BatchController {

    private final UserAuthenticationService authService;
    private final BatchService batchService;

    @GetMapping("/messages/{chatId}")
    public List<BatchMessage> getMessages(@PathVariable Long chatId) {
        User user = authService.getAuthenticatedUser();
        return batchService.getMessages(user, chatId);
    }

    @GetMapping("/chats")
    public List<BatchChat> getChats() {
        User user = authService.getAuthenticatedUser();
        return batchService.getChats(user);
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BatchMessage {
        private UserDto sender;
        private Message message;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BatchChat {
        private Chat chat;
        private GroupChatInfo group;
        private UserDto personal;
        private UnseenChatDto unseen;
        private UserDto sender;
        private Message lastMessage;
    }

}
