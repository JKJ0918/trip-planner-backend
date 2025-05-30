package com.tripPlanner.project.service.TravelJournal;

import com.tripPlanner.project.dto.TravelJournal.JournalDTO;
import com.tripPlanner.project.dto.TravelJournal.PinDTO;
import com.tripPlanner.project.dto.TravelJournal.TravelJournalRequestDTO;
import com.tripPlanner.project.entity.TravelJournal.JournalEntity;
import com.tripPlanner.project.entity.TravelJournal.PhotoEntity;
import com.tripPlanner.project.entity.TravelJournal.PinEntity;
import com.tripPlanner.project.entity.TravelJournal.TravelJournalEntity;
import com.tripPlanner.project.entity.UserEntity;
import com.tripPlanner.project.repository.TravelJournal.TravelJournalRepository;
import com.tripPlanner.project.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;


@Service
@RequiredArgsConstructor
public class TravelJournalService {

    private final UserRepository userRepository;
    private final TravelJournalRepository travelJournalRepository;

    public Long saveTravelJournal(TravelJournalRequestDTO requestDTO) throws IllegalAccessException {
        System.out.println("userId 값 확인: " + requestDTO.getUserId());
        // 1. 유저조회
        UserEntity user = userRepository.findById(Long.parseLong(requestDTO.getUserId()))
                .orElseThrow(() -> new IllegalAccessException("유저 없음"));

        // 2. TravelJournalEntity 생성
        TravelJournalEntity travelJournalEntity = TravelJournalEntity.builder()
                .user(user)
                .startDate(LocalDate.parse(requestDTO.getStartDate()))
                .endDate(LocalDate.parse(requestDTO.getEndDate()))
                .build();

        System.out.println("pinDTO 값 확인: " + requestDTO.getPins());

        // 3. Pins 추가
        if (requestDTO.getPins() != null){
            for(PinDTO pinDTO : requestDTO.getPins()){
                PinEntity pin = PinEntity.builder()
                        .lat(pinDTO.getLat())
                        .lng(pinDTO.getLng())
                        .name(pinDTO.getName())
                        .category(pinDTO.getCategory())
                        .address(pinDTO.getAddress())
                        .travelJournalEntity(travelJournalEntity)
                        .build();

                if (travelJournalEntity.getPinEntities() == null) {
                    travelJournalEntity.setPinEntities(new ArrayList<>());
                }

                travelJournalEntity.getPinEntities().add(pin);
            }
        }

        // 4. Journals 추가
        if (requestDTO.getJournals() != null) {
            for (JournalDTO dto : requestDTO.getJournals()) {
                JournalEntity journalEntity = JournalEntity.builder()
                        .date(LocalDate.parse(dto.getDate()))
                        .title(dto.getTitle())
                        .description(dto.getDescription())
                        .travelJournalEntity(travelJournalEntity)
                        .build();

                // Photo 매핑
                if (dto.getPhotos() != null) {
                    for (String url : dto.getPhotos()) {
                        PhotoEntity p = PhotoEntity.builder()
                                .url(url)
                                .journalEntity(journalEntity)
                                .build();
                        journalEntity.getPhotos().add(p);
                    }
                }

                travelJournalEntity.getJournalEntities().add(journalEntity);

            }
        }

        travelJournalRepository.save(travelJournalEntity);
        return travelJournalEntity.getId(); // 저장 후 PK 반환


    }


}
