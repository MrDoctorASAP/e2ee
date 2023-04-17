package com.e2ee.api.service;

import com.e2ee.api.repository.ChatMemberRepository;
import com.e2ee.api.repository.entities.Chat;
import com.e2ee.api.repository.entities.ChatMember;
import com.e2ee.api.service.exceptons.MemberException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@AllArgsConstructor
public class MemberService {

    private final ChatMemberRepository memberRepository;

    public boolean isChatMember(Long userId, Long chatId) {
        return memberRepository.existsByChatIdAndUserId(chatId, userId);
    }

    public void checkIsChatMember(Long userId, Long chatId) {
        if (!isChatMember(userId, chatId)) {
            throw new MemberException("User not a chat member");
        }
    }

    public List<ChatMember> getChatMembers(Long chatId) {
        return memberRepository.findAllByChatId(chatId);
    }

    public void setChatMembers(Chat chat, List<Long> userIds) {
        Objects.requireNonNull(chat.getId());
        memberRepository.saveAll(
                userIds.stream()
                        .map(ChatMember.mapping(chat.getId()))
                        .toList()
        );
        log.info("Set chat members {} to chat {}", userIds, chat);
    }

}
