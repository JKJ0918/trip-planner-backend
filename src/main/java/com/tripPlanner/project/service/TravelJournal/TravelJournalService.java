package com.tripPlanner.project.service.TravelJournal;

import com.tripPlanner.project.dto.TravelJournal.*;
import com.tripPlanner.project.entity.TravelJournal.JournalEntity;
import com.tripPlanner.project.entity.TravelJournal.PhotoEntity;
import com.tripPlanner.project.entity.TravelJournal.PinEntity;
import com.tripPlanner.project.entity.TravelJournal.TravelJournalEntity;
import com.tripPlanner.project.entity.UserEntity;
import com.tripPlanner.project.repository.TravelJournal.TravelJournalRepository;
import com.tripPlanner.project.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class TravelJournalService {

    private final UserRepository userRepository;
    private final TravelJournalRepository travelJournalRepository;

    public Long saveTravelJournal(TravelJournalRequestDTO requestDTO) throws IllegalAccessException {

        // 1. 유저조회
        UserEntity user = userRepository.findById(Long.parseLong(requestDTO.getUserId()))
                .orElseThrow(() -> new IllegalAccessException("유저 없음"));

        // 2. TravelJournalEntity 생성
        TravelJournalEntity travelJournalEntity = TravelJournalEntity.builder()
                .user(user)
                .startDate(LocalDate.parse(requestDTO.getStartDate()))
                .endDate(LocalDate.parse(requestDTO.getEndDate()))
                .title(requestDTO.getTitle())
                .locationSummary(requestDTO.getLocationSummary())
                .isPublic(requestDTO.getIsPublic())
                .build();

        // 3. Pins 추가
        if (requestDTO.getPins() != null){
            for(PinDTO pinDTO : requestDTO.getPins()){
                PinEntity pin = PinEntity.builder()
                        .lat(pinDTO.getLat())
                        .lng(pinDTO.getLng())
                        .name(pinDTO.getName())
                        .category(pinDTO.getCategory())
                        .address(pinDTO.getAddress())
                        .travelJournalPinEntity(travelJournalEntity) // TravelJournalEntity의 자식 리스트에 PinEntity를 추가
                        .build();

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

    // 게시글 가져오기 페이지
    public Page<TravelPostSummaryDTO> getPublicJournals(int page, int size, String keyword) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        Page<TravelJournalEntity> journals;

        if (keyword != null && !keyword.isBlank()) {
            journals = travelJournalRepository.searchPublicByKeyword(keyword, pageable);
        } else {
            journals = travelJournalRepository.findByIsPublicTrue(pageable);
        }

        return journals.map(journal -> new TravelPostSummaryDTO(
                        journal.getId(),
                        journal.getTitle(),
                        journal.getLocationSummary(),
                        extractThumbnail(journal),
                        journal.getUser().getNickname(),
                        journal.getCreatedAt()
                ));
    }

    // 상세페이지_특정게시물 가져오기
    public TravelPostDetailDTO getPostDetailById(Long id) {
        TravelJournalEntity journal = travelJournalRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글이 존재하지 않습니다."));

        List<PinDTO> pins = journal.getPinEntities().stream().map(pin -> new PinDTO(
                pin.getLat(), pin.getLng(), pin.getName(), pin.getAddress(), pin.getCategory()
        )).toList(); // pin 정보

        AtomicInteger dayCounter = new AtomicInteger(1);
        List<ItineraryDTO> itinerary = journal.getJournalEntities().stream()
                .map(item -> new ItineraryDTO(
                        dayCounter.getAndIncrement(),                     // 날짜를 int로 변환
                        item.getTitle(),
                        item.getDescription(),
                        item.getPhotos().stream()
                                .map(PhotoEntity::getUrl)
                                .toList()
                )).toList();
        // 썸네일 동적 추출
        String thumbnailUrl = null;
        
        List<JournalEntity> journals = journal.getJournalEntities();
        if (!journals.isEmpty() && !journals.get(0).getPhotos().isEmpty()) {
            thumbnailUrl = journals.get(0).getPhotos().get(0).getUrl(); // 첫 사진
        }

        return new TravelPostDetailDTO(
                journal.getId(),
                journal.getTitle(),
                journal.getLocationSummary(),
                new DateRangeDTO(journal.getStartDate().toString(), journal.getEndDate().toString()),
                thumbnailUrl, // 경로 확인 필수
                journal.getUser().getNickname(), // 유저 테이블과 연관됨
                pins,
                itinerary
        );
    }


    // 썸네일 추출
    private String extractThumbnail(TravelJournalEntity journal) {
        if (journal.getJournalEntities() != null && !journal.getJournalEntities().isEmpty()) {
            for (JournalEntity entry : journal.getJournalEntities()) {
                if (entry.getPhotos() != null && !entry.getPhotos().isEmpty()) {
                    return entry.getPhotos().get(0).getUrl(); // ✅ 가장 먼저 발견된 사진
                }
            }
        }
        return "https://your-default-thumbnail.com/default.jpg"; // 썸네일 없을 경우 기본값
    }




}
