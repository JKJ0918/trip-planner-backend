package com.tripPlanner.project.repository.chat;

import com.tripPlanner.project.entity.chat.ChatRoomMemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ChatRoomMemberRepository extends JpaRepository<ChatRoomMemberEntity, Long> {

    // 기존 채팅방이 있을 경우 새 채팅방을 만들지 않기 위해
    // 두 사용자(myId, targetId)가 모두 멤버로 포함된 방의 id 조회
    // 3인 이상의 채팅방(단체톡방)의 경우도 인식이 되는지 확인필요 단체톡방은 다른 이야기임
    @Query("""
        select m.chatRoomEntity.id
        from ChatRoomMemberEntity m
        where m.userEntity.id in (:a, :b)
        group by m.chatRoomEntity.id
        having count(distinct m.userEntity.id) = 2
    """)
    List<Long> findDmRoomIdsBetween(@Param("a") Long a, @Param("b")Long b);
    
}
