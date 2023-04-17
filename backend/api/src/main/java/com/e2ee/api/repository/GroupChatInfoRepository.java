package com.e2ee.api.repository;

import com.e2ee.api.repository.entities.GroupChatInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupChatInfoRepository extends JpaRepository<GroupChatInfo, Long> {

}
