package com.e2ee.api.controller;

import com.e2ee.api.controller.dto.MessageDto;
import com.e2ee.api.repository.entities.Message;
import com.e2ee.api.repository.entities.User;
import com.e2ee.api.service.MessageService;
import com.e2ee.api.service.UserAuthenticationService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/message")
public class MessageController {

    private final MessageService messageService;
    private final UserAuthenticationService authService;

    @PostMapping("/send")
    public Message sendMessage(@RequestBody MessageDto message) {
        User user = authService.getAuthenticatedUser();
        return messageService.sendMessage(user, message);
    }

//    @GetMapping("/messages")
//    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
//    public List<Message> getMessages(@RequestParam Long chatId, Optional<Integer> page, Optional<Integer> count) {
//        User user = authService.getAuthenticatedUser();
//        return chatService.getMessages(user, chatId, page.orElse(0), count.orElse(Integer.MAX_VALUE));
//    }

}
