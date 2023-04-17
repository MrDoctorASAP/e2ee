package com.e2ee.api.service;

import com.e2ee.api.controller.dto.GroupChatDto;
import com.e2ee.api.controller.dto.PersonalChatDto;
import com.e2ee.api.repository.*;
import com.e2ee.api.repository.entities.*;
import com.e2ee.api.service.exceptons.ChatException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Stream;

@Slf4j
@Service
@AllArgsConstructor
public class ChatService {

    private final ChatRepository chatRepository;
    private final ChatMemberRepository memberRepository;
    private final UserRepository userRepository;
    private final GroupChatInfoRepository infoRepository;

    private final UserService userService;
    private final MemberService memberService;

    @Transactional
    public Chat createPersonalChat(User user, PersonalChatDto personalChat) {
        userService.checkExistsById(personalChat.getUserId());
        Chat chat = chatRepository.save(Chat.createPersonalChat());
        memberService.setChatMembers(chat, List.of(user.getId(), personalChat.getUserId()));
        log.info("Create personal char: {}", chat);
        return chat;
    }

    @Transactional
    public Chat createGroupChat(User user, GroupChatDto groupChat) {
        List<Long> userIds = groupChat.getUsers();
        userService.checkExistsByIds(userIds);
        userIds = Stream.concat(Stream.of(user.getId()), userIds.stream()).distinct().toList();
        Chat chat = chatRepository.save(Chat.createGroupChat());
        log.info("Create group chat: {}", chat);
        memberService.setChatMembers(chat, userIds);
//        memberRepository.saveAll(userIds.stream().map(ChatMember.mapping(chat.getId())).toList());
        infoRepository.save(new GroupChatInfo(null, chat.getId(), groupChat.getName()));
        return chat;
    }

    public List<Long> getChats(User user, Integer page, Integer count) {
        return memberRepository.findAllByUserId(user.getId(), PageRequest.of(page, count))
                .stream()
                .map(ChatMember::getChatId)
                .toList();
    }

    public boolean chatExists(Long chatId) {
        return chatRepository.existsById(chatId);
    }

    public void checkChatExists(Long chatId) {
        if (!chatExists(chatId)) {
            throw new ChatException("Chat not found");
        }
    }

}
