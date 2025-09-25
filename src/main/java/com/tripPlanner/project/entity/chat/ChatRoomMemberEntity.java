package com.tripPlanner.project.entity.chat;

import com.tripPlanner.project.entity.UserEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Entity
@Getter @Setter
@Table(name ="chat_room_member")
@NoArgsConstructor
public class ChatRoomMemberEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 단일 PK

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chatroom_id")
    private ChatRoomEntity chatRoomEntity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserEntity userEntity;

    private String nickname; // user에서 뽑아오면 되는거 아닌가 싶음

    // 사용자가 해당 방에서 마지막으로 읽은 시각
    // null이면 방 입장 전/기록 없음 → 전부 미확인 처리
    private Date lastReadAt;

    public ChatRoomMemberEntity(ChatRoomEntity room, UserEntity user, String nickname) {
        this.chatRoomEntity = room;
        this.userEntity = user;
        this.nickname = nickname;
    }
}
