package com.e2ee.api.controller;

import com.e2ee.api.controller.dto.secure.RecipientKeyDto;
import com.e2ee.api.controller.dto.secure.AcceptedSecureChatDto;
import com.e2ee.api.controller.dto.secure.SecureChatDto;
import com.e2ee.api.controller.dto.secure.SecureChatIdDto;
import com.e2ee.api.controller.dto.secure.SecureChatInviteDto;
import com.e2ee.api.repository.entities.User;
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

    @PostMapping("/exchange")
    public List<RecipientKeyDto> exchange(@RequestBody List<SecureChatIdDto> secureChatIds) {
        User user = authService.getAuthenticatedUser();
        return keyExchangeService.exchange(user, secureChatIds);
    }

    @PostMapping("/complete")
    public void complete(@RequestBody SecureChatIdDto secureChatId) {
        User user = authService.getAuthenticatedUser();
        keyExchangeService.complete(user, secureChatId);
    }

    // send
    // messages
    // seen

}
