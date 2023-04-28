package com.e2ee.api.service;

import com.e2ee.api.controller.dto.secure.SecureChatDto;
import com.e2ee.api.repository.entities.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class SecureChatIdService {
    public String generateSecureChatId(User user, SecureChatDto secureChat) {
        return UUID.randomUUID().toString();
    }
}
