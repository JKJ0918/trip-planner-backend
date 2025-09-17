package com.tripPlanner.project.entity.chat;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Getter
@Table(name = "chat_room")
@NoArgsConstructor
public class ChatRoomEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private Date newDate;

    public ChatRoomEntity(String title, Date newDate){
        this.title = title;
        this.newDate = newDate;
    }

}
