package com.e2ee.api.controller;

import com.e2ee.api.controller.dto.UnseenChatDto;
import com.e2ee.api.repository.entities.User;
import com.e2ee.api.service.UnseenMessagesService;
import com.e2ee.api.service.UserAuthenticationService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/unseen")
public class UnseenMessagesController {

    private final UnseenMessagesService unseenService;
    private final UserAuthenticationService authService;

    @GetMapping("/chats")
    public List<UnseenChatDto> getUnseenChats() {
        User user = authService.getAuthenticatedUser();
        return unseenService.getUnseenChats(user);
    }

    @GetMapping("/seen")
    public void seen(@RequestParam Long chatId) {
        User user = authService.getAuthenticatedUser();
        unseenService.seen(user, chatId);
    }

}
