package com.e2ee.api.controller.dto;

import com.e2ee.api.repository.entities.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GroupChatDto {

    @NotNull
    @NotBlank
    @Pattern(regexp = "\\w+")
    @Size(min = 1, max = 255)
    private String name;

    @NotNull
    private List<Long> users;

    public static GroupChatDto create(String name, User... users) {
        return new GroupChatDto(name, Arrays.stream(users).map(User::getId).toList());
    }

}

