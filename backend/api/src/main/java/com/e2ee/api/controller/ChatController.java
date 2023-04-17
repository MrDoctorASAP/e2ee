package com.e2ee.api.controller;

import com.e2ee.api.controller.dto.GroupChatDto;
import com.e2ee.api.controller.dto.LastMessageDto;
import com.e2ee.api.repository.entities.Chat;
import com.e2ee.api.repository.entities.User;
import com.e2ee.api.service.ChatService;
import com.e2ee.api.service.UserAuthenticationService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/chat")
public class ChatController {

    private final ChatService chatService;
    private final UserAuthenticationService authService;

    @GetMapping("/chats")
    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public List<Long> getChats(@RequestParam Optional<Integer> page,
                               @RequestParam Optional<Integer> count) {
        User user = authService.getAuthenticatedUser();
        return chatService.getChats(user, page.orElse(0), count.orElse(Integer.MAX_VALUE));
    }

//    @GetMapping("/last")
//    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
//    public List<LastMessageDto> getLastMessages(@RequestParam Optional<Integer> page,
//                                                @RequestParam Optional<Integer> count) {
//        User user = authService.getAuthenticatedUser();
//        return chatService.getLastMessages(user, page.orElse(0), count.orElse(Integer.MAX_VALUE));
//    }

    @PostMapping("/create")
    public Chat createChat(@RequestBody GroupChatDto chat) {
        var user = authService.getAuthenticatedUser();
        return chatService.createGroupChat(user, chat);
    }

}
