package com.e2ee.api.controller.dto.secure;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AcceptedSecureChatDto {
    private String secureChatId;
    private String publicKey;
}
