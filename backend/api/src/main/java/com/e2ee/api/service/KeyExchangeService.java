package com.e2ee.api.service;

import com.e2ee.api.controller.dto.secure.*;
import com.e2ee.api.repository.SecureChatAcceptRepository;
import com.e2ee.api.repository.SecureChatInviteRepository;
import com.e2ee.api.repository.SecureChatMemberRepository;
import com.e2ee.api.repository.SecureChatMessageRepository;
import com.e2ee.api.repository.entities.secure.SecureChatAccept;
import com.e2ee.api.repository.entities.secure.SecureChatInvite;
import com.e2ee.api.repository.entities.secure.SecureChatMember;
import com.e2ee.api.repository.entities.User;
import com.e2ee.api.repository.entities.secure.SecureChatMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.function.Predicate;

@Slf4j
@Service
@RequiredArgsConstructor
public class KeyExchangeService {

    private final UserService userService;
    private final SecureChatIdService idService;
    private final UserProfileService profileService;

    private final SecureChatMemberRepository memberRepository;
    private final SecureChatInviteRepository inviteRepository;
    private final SecureChatAcceptRepository acceptRepository;
    private final SecureChatMessageRepository messageRepository;

    private final MessagingService messagingService;

    @Transactional
    public SecureChatIdDto initSecureChat(User user, SecureChatDto secureChat) {
        log.info("initSecureChat: {}", secureChat);
        userService.checkExistsById(secureChat.getRecipientId());
        String secureChatId = idService.generateSecureChatId(user, secureChat);
        memberRepository.saveAll(List.of(
                new SecureChatMember(secureChatId, user.getId()),
                new SecureChatMember(secureChatId, secureChat.getRecipientId())
        ));

        SecureChatInvite invite = inviteRepository.save(new SecureChatInvite(
                user.getId(),
                secureChat.getRecipientId(),
                secureChatId,
                secureChat.getPublicKey()
        ));

        log.info("Create invite: {}", invite);

        messagingService.publish(getEventRecipient(user, secureChatId),
                new SecureChatInviteDto(invite.getSecureChatId(),
                invite.getPublicKey(),
                profileService.getUser(invite.getSenderId())));

        return new SecureChatIdDto(secureChatId);
    }

    @Transactional
    public List<SecureChatInviteDto> getInvites(User user) {
        return inviteRepository
                .findAllByRecipientId(user.getId())
                .stream()
                .map(invite -> new SecureChatInviteDto(invite.getSecureChatId(),
                        invite.getPublicKey(),
                        profileService.getUser(invite.getSenderId())))
                .toList();
    }

    @Transactional
    public void accept(User user, AcceptedSecureChatDto accept) {
        acceptRepository.save(new SecureChatAccept(accept.getSecureChatId(), accept.getPublicKey()));
        inviteRepository.deleteBySecureChatId(accept.getSecureChatId());
        messagingService.publish(getEventRecipient(user, accept.getSecureChatId()),
                new RecipientKeyDto(accept.getSecureChatId(), accept.getPublicKey()));
    }

    @Transactional
    public List<RecipientKeyDto> exchange(User user) {
        List<String> secureChatIds = memberRepository.findAllByUserId(user.getId()).stream()
                .map(SecureChatMember::getSecureChatId)
                .toList();
        return acceptRepository.findAllBySecureChatIdIn(secureChatIds)
                .stream()
                .map(RecipientKeyDto.mapping())
                .toList();
    }

    @Transactional
    public void complete(User user, SecureChatIdDto secureChatId) {
        acceptRepository.deleteBySecureChatId(secureChatId.getSecureChatId());
    }

    @Transactional
    public SecureChatMessage sendMessage(User user, SecureChatMessageDto message) {
        SecureChatMessage chatMessage = messageRepository.save(message.toEntry(user.getId()));
        messagingService.publish(getEventRecipient(user, message.getSecureChatId()), chatMessage);
        return chatMessage;
    }

    @Transactional
    public List<SecureChatMessage> getMessages(User user, List<String> chatIds) {
        return chatIds.stream()
                .flatMap(chatId -> messageRepository.findAllBySecureChatId(chatId).stream())
                .filter(message -> !message.getSenderId().equals(user.getId()))
                .toList();
    }

    @Transactional
    public void deleteMessages(User user, List<Long> messageIds) {
        messageRepository.deleteAllByIdIn(messageIds);
    }

    private Long getEventRecipient(User user, String chatId) {
        return memberRepository.findAllBySecureChatId(chatId)
                .stream()
                .map(SecureChatMember::getUserId)
                .filter(Predicate.not(user.getId()::equals))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Corrupt members"));
    }

}
