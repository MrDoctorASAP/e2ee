package com.e2ee.api.service;

import com.e2ee.api.TestSupportService;
import com.e2ee.api.repository.entities.Chat;
import com.e2ee.api.repository.entities.ChatMember;
import com.e2ee.api.repository.entities.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.stream.Stream;

import static com.e2ee.api.controller.dto.GroupChatDto.create;
import static com.e2ee.api.controller.dto.PersonalChatDto.with;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.*;

@SpringBootTest
class ChatServiceTest {

    @Autowired ChatService chatService;
    @Autowired TestSupportService testSupport;

    @Test
    void createPersonalChat() {

        User user1 = testSupport.createUser();
        User user2 = testSupport.createUser();

        Chat chat = chatService.createPersonalChat(user1, with(user2));

        Chat chatUser1 = chatService.getChat(user1, chat.getId());
        Chat chatUser2 = chatService.getChat(user2, chat.getId());

        assertThat(chat.isPersonal(), is(true));
        assertThat(chat.getGroupChatInfo(), is(nullValue()));

        assertThat(chat, is(equalTo(chatUser1)));
        assertThat(chat, is(equalTo(chatUser2)));
    }

    @Test
    void getChatMembers_Personal() {
        User user1 = testSupport.createUser();
        User user2 = testSupport.createUser();
        Chat chat = chatService.createPersonalChat(user1, with(user2));

        List<Long> actualMembersIds = chatService.getChatMembers(user1, chat.getId())
                .stream().map(ChatMember::getUserId).sorted().toList();
        List<Long> expectedMembersIds = Stream.of(user1.getId(), user2.getId()).sorted().toList();

        assertThat(actualMembersIds, equalTo(expectedMembersIds));
    }

    @Test
    void getChatMembers_Group() {

        User user1 = testSupport.createUser();
        User user2 = testSupport.createUser();
        User user3 = testSupport.createUser();

        String chatName = "GroupChat1";
        Chat groupChat = chatService.createGroupChat(user1, create(chatName, user2, user3));

        List<Long> actualMembersIds = chatService.getChatMembers(user1, groupChat.getId())
                .stream().map(ChatMember::getUserId).sorted().toList();
        List<Long> expectedMembersIds = Stream.of(user1.getId(), user2.getId(), user3.getId())
                .sorted().toList();

        assertThat(actualMembersIds, equalTo(expectedMembersIds));
    }

    @Test
    void createGroupChat() {

        User user1 = testSupport.createUser();
        User user2 = testSupport.createUser();
        User user3 = testSupport.createUser();

        String chatName = "GroupChat1";
        Chat groupChat = chatService.createGroupChat(user1, create(chatName, user2, user3));
        assertThat(groupChat.isPersonal(), is(false));
        assertThat(groupChat.getGroupChatInfo().getName(), is(equalTo(chatName)));
        assertThat(groupChat.getGroupChatInfo().getOwnerId(), is(equalTo(user1.getId())));

        assertThat(groupChat, is(equalTo(chatService.getChat(user1, groupChat.getId()))));
        assertThat(groupChat, is(equalTo(chatService.getChat(user2, groupChat.getId()))));
        assertThat(groupChat, is(equalTo(chatService.getChat(user3, groupChat.getId()))));
    }


    @Test
    void getChats() {

        User user1 = testSupport.createUser();
        User user2 = testSupport.createUser();
        User user3 = testSupport.createUser();

        Chat chat12 = chatService.createPersonalChat(user1, with(user2));
        Chat chat123 = chatService.createGroupChat(user2, create("123", user1, user3));
        Chat chat23 = chatService.createPersonalChat(user3, with(user2));

        assertThat(chatService.getChats(user1), containsInAnyOrder(chat12, chat123));
        assertThat(chatService.getChats(user2), containsInAnyOrder(chat12, chat123, chat23));
        assertThat(chatService.getChats(user3), containsInAnyOrder(chat123, chat23));

    }

}