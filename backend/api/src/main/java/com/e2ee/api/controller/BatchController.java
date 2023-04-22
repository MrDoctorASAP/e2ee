package com.e2ee.api.controller;

import com.e2ee.api.controller.dto.BatchChat;
import com.e2ee.api.controller.dto.BatchMessages;
import com.e2ee.api.controller.dto.FlatUnseenChat;
import com.e2ee.api.controller.dto.UserDto;
import com.e2ee.api.repository.batch.FlatBatchChat;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
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

    @GetMapping("/chat/{chatId}")
    public BatchMessages getMessages(@PathVariable Long chatId) {
        User user = authService.getAuthenticatedUser();
        return batchService.getMessages(user, chatId, 0, Integer.MAX_VALUE);
    }

    @GetMapping("/chats")
    public List<BatchChat> getChats() {
        User user = authService.getAuthenticatedUser();
        return batchService.getChats(user);
    }

}
