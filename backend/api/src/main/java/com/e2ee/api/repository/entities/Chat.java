package com.e2ee.api.repository.entities;

import com.e2ee.api.controller.dto.GroupChatDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Chat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private boolean personal;

    @OneToOne
    @JoinColumn(name = "id", referencedColumnName = "chat_id")
    private GroupChatInfo groupChatInfo;

    public static Chat createPersonalChat() {
        return new Chat(null, true, null);
    }

    public static Chat createGroupChat() {
        return new Chat(null, false, null);
    }

}
