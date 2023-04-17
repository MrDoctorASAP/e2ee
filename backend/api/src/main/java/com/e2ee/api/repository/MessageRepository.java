package com.e2ee.api.repository;

import com.e2ee.api.repository.entities.Message;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findAllByChatId(Long chatId, Pageable pageable);
    List<Message> findAllByChatId(Long chatId);
}
