package com.tripPlanner.project.entity.travelJournal;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PinEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private double lat;
    private double lng;
    private String name;
    private String category;
    private String address;
    private String minCost;
    private String maxCost;
    private String currency;
    private String openTime;
    private String closeTime;
    @Column(length = 1000)
    private String description;

    // 이미지 URL을 리스트로 저장 (DB에 별도 테이블 생성)
    @ElementCollection // 엔티티가 아닌 **값 타입(List<String>)**을 매핑할 때 사용 **값 타입을 컬렉션에 담아서 사용하는 것을 값 타입 컬렉션이라고 한다.
    @CollectionTable(name = "pin_images", joinColumns = @JoinColumn(name = "pin_id"))
    //pin_images라는 이름의 별도 테이블이 생성됨
    //
    //joinColumns는 이 컬렉션 테이블이 어떤 엔티티(PinEntity)의 ID와 연관되어 있는지를 지정
    @Column(name = "image_url") // pin_images 테이블의 컬럼 이름을 image_url로 지정
    private List<String> images;

    @ManyToOne(fetch = FetchType.LAZY)
    private TravelJournalEntity travelJournalPinEntity;
}
