package com.e2ee.api.controller.dto.secure;

import com.e2ee.api.repository.entities.secure.SecureChatMessage;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SecureChatMessageDto {
    private String secureChatId;
    private String message;
    private String iv;

    public SecureChatMessage toEntry() {
        return new SecureChatMessage(secureChatId, message, iv);
    }
}
