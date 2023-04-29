package com.e2ee.api.controller.dto.secure;

import com.e2ee.api.controller.dto.UserDto;
import com.e2ee.api.repository.entities.secure.SecureChatInvite;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.function.Function;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SecureChatInviteDto {

    private String secureChatId;
    private String publicKey;
    private UserDto sender;

    public static Function<SecureChatInvite, SecureChatInviteDto> mapping(UserDto sender) {
        return invite -> new SecureChatInviteDto(invite.getSecureChatId(), invite.getPublicKey(), sender);
    }
}
