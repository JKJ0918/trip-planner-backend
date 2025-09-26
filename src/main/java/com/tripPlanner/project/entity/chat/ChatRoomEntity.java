package com.tripPlanner.project.entity.chat;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "chat_room")
@NoArgsConstructor
public class ChatRoomEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private Date newDate;

    @OneToMany(
            mappedBy = "chatRoomEntity",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @OrderBy("joinedAt ASC") // 선택: 정렬
    private List<ChatRoomMemberEntity> members = new ArrayList<>();

    private String lastMessage; // 마지막 메시지

    private Date lastMessageAt; // 마지막 메시지 보낸 날짜

    public ChatRoomEntity(String title, Date newDate){
        this.title = title;
        this.newDate = newDate;
    }

}
