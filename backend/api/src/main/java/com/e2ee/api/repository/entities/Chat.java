package com.e2ee.api.repository.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.Hibernate;

import java.util.Objects;

@Getter
@Setter
@ToString
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Chat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private boolean personal;

    @OneToOne
    @JoinColumn(name = "chat_id")
    private GroupChatInfo groupChatInfo;

    public static Chat createPersonalChat() {
        return new Chat(null, true, null);
    }

    public static Chat createGroupChat() {
        return new Chat(null, false, null);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Chat chat = (Chat) o;
        return id != null && Objects.equals(id, chat.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
