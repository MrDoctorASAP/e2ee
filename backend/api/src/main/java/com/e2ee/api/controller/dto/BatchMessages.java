package com.e2ee.api.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BatchMessages {
    private List<UserDto> members;
    private List<ShortMessage> messages;
}
