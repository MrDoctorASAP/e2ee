package com.e2ee.api.service;

import com.e2ee.api.controller.dto.GroupChatDto;
import com.e2ee.api.controller.dto.PersonalChatDto;
import com.e2ee.api.repository.*;
import com.e2ee.api.repository.entities.*;
import com.e2ee.api.service.exceptons.ChatException;
import com.e2ee.api.service.exceptons.MemberException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Slf4j
@Service
@AllArgsConstructor
public class ChatService {

    private final ChatRepository chatRepository;
    private final ChatMemberRepository memberRepository;
    private final GroupChatInfoRepository infoRepository;

    private final UserService userService;

    @Transactional
    public Chat createPersonalChat(User user, PersonalChatDto personalChat) {
        userService.checkUserExists(user);
        Chat chat = chatRepository.save(Chat.createPersonalChat());
        List<ChatMember> chatMembers = memberRepository.saveAll(List.of(
                new ChatMember(chat.getId(), user.getId()),
                new ChatMember(chat.getId(), personalChat.getUserId())
        ));
        log.info("Create personal char: {} {}", chat, chatMembers);
        return chat;
    }

    @Transactional
    public Chat createGroupChat(User user, GroupChatDto groupChat) {

        Set<Long> userIds = new HashSet<>(groupChat.getUsers());
        userIds.add(user.getId());

        Chat chat = Chat.createGroupChat();
        Chat save = chatRepository.save(chat);

        GroupChatInfo groupChatInfo = GroupChatInfo.builder()
                .chatId(save.getId())
                .name(groupChat.getName())
                .ownerId(user.getId())
                .build();
        groupChatInfo = infoRepository.save(groupChatInfo);
        List<ChatMember> chatMembers = memberRepository.saveAll(
                userIds.stream()
                        .map(ChatMember.mapping(save.getId()))
                        .toList()
        );

        chat.setId(save.getId());
        chat.setGroupChatInfo(groupChatInfo);
        log.info("Create group chat: {} {} {}", chat, chatMembers, groupChatInfo);
        return chat;
    }

    public List<Chat> getChats(User user) {
        userService.checkUserExists(user);
        return chatRepository.findAllByUserId(user.getId());
    }

    public List<ChatMember> getChatMembers(User user, Long chatId) {
        checkChatExistsById(chatId);
        checkIsChatMember(user.getId(), chatId);
        return getChatMembers(chatId);
    }

    List<ChatMember> getChatMembers(Long chatId) {
        return memberRepository.findAllByChatId(chatId);
    }

    public boolean isChatMember(Long userId, Long chatId) {
        return memberRepository.existsByChatIdAndUserId(chatId, userId);
    }

    public void checkIsChatMember(Long userId, Long chatId) {
        if (!isChatMember(userId, chatId)) {
            throw new MemberException("User not a chat member");
        }
    }

    public boolean chatExists(Long chatId) {
        return chatRepository.existsById(chatId);
    }

    public void checkChatExistsById(Long chatId) {
        if (!chatExists(chatId)) {
            throw new ChatException("Chat not found");
        }
    }

    public Chat getChat(User user, Long chatId) {
        checkChatExistsById(chatId);
        checkIsChatMember(user.getId(), chatId);
        return chatRepository.findById(chatId)
                .orElseThrow(() -> new ChatException("Chat not found"));
    }

}
