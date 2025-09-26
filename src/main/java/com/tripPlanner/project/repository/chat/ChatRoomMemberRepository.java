package com.tripPlanner.project.repository.chat;

import com.tripPlanner.project.entity.chat.ChatRoomMemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

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

    // 채팅방 아이디로 userId 목록 반환
    @Query("""
      select m.userEntity.id
      from ChatRoomMemberEntity m
      where m.chatRoomEntity.id = :roomId
    """)
    List<Long> findUserIdsByRoomId(@Param("roomId") Long roomId);

    // 채팅방 아이디로 채팅방 유저 Entity 반환, lastReadAt에 사용 예정
    @Query("""
      select m
      from ChatRoomMemberEntity m
      where m.chatRoomEntity.id = :roomId
    """)
    List<ChatRoomMemberEntity> findMembersByRoomId(@Param("roomId") Long roomId);

    Optional<ChatRoomMemberEntity> findByChatRoomEntity_IdAndUserEntity_Id(Long roomId, Long userId);

}
