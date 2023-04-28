package com.e2ee.api.controller.dto.secure;

import com.e2ee.api.repository.entities.secure.SecureChatAccept;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.function.Function;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecipientKeyDto {
    private String chatId;
    private String publicKey;

    public static Function<SecureChatAccept, RecipientKeyDto> mapping() {
        return accept -> new RecipientKeyDto(accept.getSecureChatId(), accept.getPublicKey());
    }
}
