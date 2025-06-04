// util/DummyData.java
package com.tripPlanner.project.controller;

import com.tripPlanner.project.dto.*;
import com.tripPlanner.project.dto.TravelJournal.DateRangeDTO;
import com.tripPlanner.project.dto.TravelJournal.ItineraryDTO;
import com.tripPlanner.project.dto.TravelJournal.PinDTO;
import com.tripPlanner.project.dto.TravelJournal.TravelPostDetailDTO;

import java.util.List;

public class DummyData {
    public static TravelPostDetailDTO getDummyDetail(Long id) {
        return new TravelPostDetailDTO(
                id,
                "싱가포르 3박 5일 자유여행",
                "싱가포르",
                new DateRangeDTO("2025-06-10", "2025-06-14"),
                "/images/trip-3.jpg",
                "여행러버",
                List.of(
                        new PinDTO(1.283, 103.860, "Marina Bay Sands", "10 Bayfront Ave, Singapore", "랜드마크")
                ),
                List.of(
                        new ItineraryDTO(
                                1,
                                "센토사 탐험",
                                "센토사 케이블카 타기 → 루지 → 해변 산책",
                                List.of(
                                        "/images/itinerary/day1-1.jpg",
                                        "/images/itinerary/day1-2.jpg"
                                )
                        ),
                        new ItineraryDTO(
                                2,
                                "마리나베이 중심 관광",
                                "가든스 바이 더 베이 → 슈퍼트리 쇼 → 마리나베이샌즈 야경",
                                List.of(
                                        "/images/itinerary/day2-1.jpg",
                                        "/images/itinerary/day2-2.jpg"
                                )
                        )
                )
        );
    }
}
