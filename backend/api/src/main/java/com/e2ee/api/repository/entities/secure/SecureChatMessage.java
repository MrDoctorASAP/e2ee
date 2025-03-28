package com.e2ee.api.repository.entities.secure;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;
import org.hibernate.Hibernate;

import java.util.Objects;

@Getter
@Setter
@Entity
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class SecureChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long senderId;
    private String secureChatId;
    private String message;
    private String iv;
    private Long date;

    public SecureChatMessage(Long senderId, String secureChatId, String message, String iv) {
        this.senderId = senderId;
        this.date = System.currentTimeMillis();
        this.secureChatId = secureChatId;
        this.message = message;
        this.iv = iv;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        SecureChatMessage that = (SecureChatMessage) o;
        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
