package com.e2ee.api.service;

import com.e2ee.api.TestSupportService;
import com.e2ee.api.controller.dto.secure.*;
import com.e2ee.api.repository.entities.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

@SpringBootTest
class KeyExchangeServiceTest {

    @Autowired
    TestSupportService testSupport;

    @Autowired
    KeyExchangeService exchangeService;

    @Test
    public void keyExchange() {

        ObjectMapper mapper = new ObjectMapper();
        User user1 = testSupport.createUser();
        User user2 = testSupport.createUser();

        System.out.println(" -- INIT -- ");
        SecureChatIdDto secureChatId = exchangeService.initSecureChat(user1,
                new SecureChatDto("PUBLIC_USER_1", user2.getId()));
        System.out.println(secureChatId);

        assertThat(secureChatId.getSecureChatId(), is(notNullValue()));

        List<SecureChatInviteDto> invites = exchangeService.getInvites(user2);
        assertThat(invites, hasSize(1));

        System.out.println(" -- INVITE -- ");
        SecureChatInviteDto invite = invites.get(0);
        System.out.println(invite);

        System.out.println(" -- ACCEPT -- ");
        exchangeService.accept(user2, new AcceptedSecureChatDto(invite.getSecureChatId(), "PUBLIC_USER_2"));
        assertThat(exchangeService.getInvites(user2), hasSize(0));

        List<RecipientKeyDto> exchange = exchangeService.exchange(user1);
        assertThat(exchange, hasSize(1));

        System.out.println(" --  EXCHANGE -- ");
        RecipientKeyDto recipientKeyDto = exchange.get(0);
        System.out.println(recipientKeyDto);

        System.out.println(" --  COMPLETE -- ");
        exchangeService.complete(user1, secureChatId);
        assertThat(exchangeService.exchange(user1), hasSize(0));
    }

}