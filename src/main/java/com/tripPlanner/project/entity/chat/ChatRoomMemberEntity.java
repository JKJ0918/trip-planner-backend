package com.tripPlanner.project.entity.chat;

import com.tripPlanner.project.entity.UserEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

    public ChatRoomMemberEntity(ChatRoomEntity room, UserEntity user, String nickname) {
        this.chatRoomEntity = room;
        this.userEntity = user;
        this.nickname = nickname;
    }
}
