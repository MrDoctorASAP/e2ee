package com.e2ee.api.service;

import com.e2ee.api.controller.dto.secure.*;
import com.e2ee.api.repository.SecureChatAcceptRepository;
import com.e2ee.api.repository.SecureChatInviteRepository;
import com.e2ee.api.repository.SecureChatMemberRepository;
import com.e2ee.api.repository.entities.secure.SecureChatAccept;
import com.e2ee.api.repository.entities.secure.SecureChatInvite;
import com.e2ee.api.repository.entities.secure.SecureChatMember;
import com.e2ee.api.repository.entities.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class KeyExchangeService {

    private final UserService userService;
    private final SecureChatIdService idService;

    private final SecureChatMemberRepository memberRepository;
    private final SecureChatInviteRepository inviteRepository;
    private final SecureChatAcceptRepository acceptRepository;

    @Transactional
    public SecureChatIdDto initSecureChat(User user, SecureChatDto secureChat) {
        userService.checkExistsById(secureChat.getRecipientId());
        String secureChatId = idService.generateSecureChatId(user, secureChat);
        memberRepository.saveAll(List.of(
                new SecureChatMember(secureChatId, user.getId()),
                new SecureChatMember(secureChatId, secureChat.getRecipientId())
        ));
        inviteRepository.save(new SecureChatInvite(
                secureChat.getRecipientId(),
                secureChatId,
                secureChat.getPublicKey()
        ));
        return new SecureChatIdDto(secureChatId);
    }

    public List<SecureChatInviteDto> getInvites(User user) {
        return inviteRepository
                .findAllByRecipientId(user.getId())
                .stream()
                .map(SecureChatInviteDto.mapping())
                .toList();
    }

    @Transactional
    public void accept(User user, AcceptedSecureChatDto accept) {
        acceptRepository.save(new SecureChatAccept(accept.getSecureChatId(), accept.getPublicKey()));
        inviteRepository.deleteBySecureChatId(accept.getSecureChatId());
    }

    public List<RecipientKeyDto> exchange(User user, List<SecureChatIdDto> secureChatId) {
        List<String> secureChatIds = secureChatId.stream()
                .map(SecureChatIdDto::getSecureChatId)
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

}
