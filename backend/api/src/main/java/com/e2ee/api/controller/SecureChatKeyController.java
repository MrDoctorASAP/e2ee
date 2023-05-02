package com.e2ee.api.controller;

import com.e2ee.api.controller.dto.secure.*;
import com.e2ee.api.repository.entities.Message;
import com.e2ee.api.repository.entities.User;
import com.e2ee.api.repository.entities.secure.SecureChatMessage;
import com.e2ee.api.service.KeyExchangeService;
import com.e2ee.api.service.UserAuthenticationService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/secure/chat")
public class SecureChatKeyController {

    private final UserAuthenticationService authService;
    private final KeyExchangeService keyExchangeService;

    @PostMapping("/create")
    public SecureChatIdDto create(@RequestBody SecureChatDto secureChat) {
        User user = authService.getAuthenticatedUser();
        return keyExchangeService.initSecureChat(user, secureChat);
    }

    @GetMapping("/invites")
    public List<SecureChatInviteDto> invites() {
        User user = authService.getAuthenticatedUser();
        return keyExchangeService.getInvites(user);
    }

    @PostMapping("/accept")
    public void accept(@RequestBody AcceptedSecureChatDto acceptedSecureChat) {
        User user = authService.getAuthenticatedUser();
        keyExchangeService.accept(user, acceptedSecureChat);
    }

    @GetMapping("/exchange")
    public List<RecipientKeyDto> exchange() {
        User user = authService.getAuthenticatedUser();
        return keyExchangeService.exchange(user);
    }

    @PostMapping("/complete")
    public void complete(@RequestBody SecureChatIdDto secureChatId) {
        User user = authService.getAuthenticatedUser();
        keyExchangeService.complete(user, secureChatId);
    }

    @PostMapping("/send")
    public SecureChatMessage send(@RequestBody SecureChatMessageDto message) {
        User user = authService.getAuthenticatedUser();
        return keyExchangeService.sendMessage(user, message);
    }

    @PostMapping("/messages")
    public List<SecureChatMessage> getMessages(@RequestBody List<String> secureChatIds) {
        User user = authService.getAuthenticatedUser();
        return keyExchangeService.getMessages(user, secureChatIds);
    }

    @PostMapping("/seen")
    public void seen(@RequestBody List<Long> ids) {
        User user = authService.getAuthenticatedUser();
        keyExchangeService.deleteMessages(user, ids);
    }

}
