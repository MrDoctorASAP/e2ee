package com.e2ee.api.service;

import com.e2ee.api.controller.dto.UnseenChatDto;
import com.e2ee.api.repository.ChatMemberRepository;
import com.e2ee.api.repository.UnseenMessageRepository;
import com.e2ee.api.repository.entities.ChatMember;
import com.e2ee.api.repository.entities.Message;
import com.e2ee.api.repository.entities.UnseenMessage;
import com.e2ee.api.repository.entities.User;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static java.util.function.Predicate.not;
import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;

@Service
@AllArgsConstructor
public class UnseenMessagesService {

    private final UnseenMessageRepository unseenRepository;
    private final MemberService memberService;

    public List<UnseenChatDto> getUnseenChats(User user) {
        return unseenRepository.findAllByUserId(user.getId())
                .stream()
                .collect(groupingBy(UnseenMessage::getChatId, counting()))
                .entrySet()
                .stream()
                .map(entry -> new UnseenChatDto(entry.getKey(), entry.getValue()))
                .toList();
    }

    public void seen(User user, Long chatId) {
        unseenRepository.deleteAllByUserIdAndChatId(user.getId(), chatId);
    }

    @Transactional
    public void publish(Message message) {
        List<ChatMember> members = memberService.getChatMembers(message.getChatId());
        List<UnseenMessage> unseenMessages = members.stream()
                .map(ChatMember::getUserId)
                .filter(not(message.getUserId()::equals))
                .map(UnseenMessage.mapping(message))
                .toList();
        unseenRepository.saveAll(unseenMessages);
    }

}
