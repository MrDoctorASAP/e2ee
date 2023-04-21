package com.e2ee.api.controller.dto;

import com.e2ee.api.repository.entities.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PersonalChatDto {

    private Long userId;

    public static PersonalChatDto with(User receiver) {
        return new PersonalChatDto(receiver.getId());
    }

}
