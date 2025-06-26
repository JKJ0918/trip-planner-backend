package com.tripPlanner.project.service.travelJournal;

import com.tripPlanner.project.dto.travelJournal.*;
import com.tripPlanner.project.entity.travelJournal.JournalEntity;
import com.tripPlanner.project.entity.travelJournal.PhotoEntity;
import com.tripPlanner.project.entity.travelJournal.PinEntity;
import com.tripPlanner.project.entity.travelJournal.TravelJournalEntity;
import com.tripPlanner.project.entity.UserEntity;
import com.tripPlanner.project.repository.travelJournal.TravelJournalRepository;
import com.tripPlanner.project.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
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
                .useFlight(requestDTO.getUseFlight())
                .flightDepartureAirline(requestDTO.getFlightDepartureAirline())
                .flightDepartureName(requestDTO.getFlightDepartureName())
                .flightDepartureTime(requestDTO.getFlightDepartureTime())
                .flightReturnAirline(requestDTO.getFlightReturnAirline())
                .flightReturnName(requestDTO.getFlightReturnName())
                .flightReturnTime(requestDTO.getFlightReturnTime())
                .travelTrans(requestDTO.getTravelTrans())
                .totalBudget(requestDTO.getTotalBudget())
                .travelTheme(requestDTO.getTravelTheme())
                .review(requestDTO.getReview())
                .isAfterTravel(requestDTO.getIsAfterTravel())
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
                        .images(pinDTO.getImages())
                        .minCost(pinDTO.getMinCost())
                        .maxCost(pinDTO.getMaxCost())
                        .currency(pinDTO.getCurrency())
                        .openTime(pinDTO.getOpenTime())
                        .closeTime(pinDTO.getCloseTime())
                        .description(pinDTO.getDescription())
                        .travelJournalPinEntity(travelJournalEntity)
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
                pin.getLat(),
                pin.getLng(),
                pin.getName(),
                pin.getAddress(),
                pin.getCategory(),
                pin.getImages(),               // 이미지 리스트
                pin.getMinCost(),
                pin.getMaxCost(),
                pin.getCurrency(),
                pin.getOpenTime(),
                pin.getCloseTime(),
                pin.getDescription()
        )).toList();


        AtomicInteger dayCounter = new AtomicInteger(1);
        List<JournalReadDTO> itinerary = journal.getJournalEntities().stream()
                .map(item -> new JournalReadDTO(
                        dayCounter.getAndIncrement(),                     // 날짜를 int로 변환
                        item.getTitle(),
                        item.getDescription(),
                        item.getPhotos().stream()
                                .map(PhotoEntity::getUrl)
                                .toList(),
                        item.getDate() // 일별 날짜 받아옴
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
                journal.getUseFlight(),
                journal.getFlightDepartureAirline(),
                journal.getFlightDepartureName(),
                journal.getFlightDepartureTime(),
                journal.getFlightReturnAirline(),
                journal.getFlightReturnName(),
                journal.getFlightReturnTime(),
                journal.getTravelTrans(),
                journal.getTotalBudget(),
                journal.getTravelTheme(),
                journal.getReview(),
                journal.getIsAfterTravel(),
                new DateRangeDTO(journal.getStartDate().toString(), journal.getEndDate().toString()),
                thumbnailUrl, // 경로 확인 필수
                journal.getUser().getNickname(), // 유저 테이블과 연관됨
                pins,
                itinerary
                // 아래 추가 필드들

        );
    }


    // 썸네일 추출 - posts 게시물 리스트에 PostCard.tsx의 썸네일
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

    // 게시글 수정
    @Transactional
    public void updatePost(Long id, PostUpdateDTO dto, Long userId) {
        TravelJournalEntity journal = travelJournalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("게시글이 존재하지 않습니다."));

        if (!journal.getUser().getId().equals(userId)) {
            throw new RuntimeException("작성자만 수정할 수 있습니다.");
        }

        // 기본 정보 업데이트
        journal.setTitle(dto.getTitle());
        journal.setLocationSummary(dto.getLocationSummary());
        journal.setStartDate(LocalDate.parse(dto.getDateRange().getStartDate()));
        journal.setEndDate(LocalDate.parse(dto.getDateRange().getEndDate()));

        // 핀 갱신
        journal.getPinEntities().clear();
        if (dto.getPins() != null) {
            for (PinDTO pinDTO : dto.getPins()) {
                PinEntity pin = new PinEntity();
                pin.setLat(pinDTO.getLat());
                pin.setLng(pinDTO.getLng());
                pin.setName(pinDTO.getName());
                pin.setAddress(pinDTO.getAddress());
                pin.setCategory(pinDTO.getCategory());
                pin.setTravelJournalPinEntity(journal);
                journal.getPinEntities().add(pin);
            }
        }

        // 일일 일정 갱신
        journal.getJournalEntities().clear();
        if (dto.getItinerary() != null) {
            LocalDate current = journal.getStartDate();

            for (JournalUpdateDTO entry : dto.getItinerary()) {
                JournalEntity journalEntity = new JournalEntity();
                journalEntity.setTitle(entry.getTitle());
                journalEntity.setDescription(entry.getContent());
                journalEntity.setDate(entry.getDate());
                journalEntity.setTravelJournalEntity(journal);

                // 이미지들
                List<PhotoEntity> photos = entry.getImages().stream().map(url -> {
                    PhotoEntity photo = new PhotoEntity();
                    photo.setUrl(url);
                    photo.setJournalEntity(journalEntity);
                    return photo;
                }).collect(Collectors.toList());

                journalEntity.setPhotos(photos);
                journal.getJournalEntities().add(journalEntity);

                current = current.plusDays(1); // 다음 날짜로 이동
            }
        }
    }

    // 게시글 삭제
    public void deletePost(Long journalId, Long userId) throws IllegalAccessException{
        
        TravelJournalEntity journal = travelJournalRepository.findById(journalId)
                .orElseThrow(() ->new IllegalArgumentException("존재하지 않는 게시글입니다."));

        if (!journal.getUser().getId().equals(userId)){ // Java에서는 Long == Long은 참조 비교이므로 .equals() 사용
                throw new IllegalAccessException("삭제 권한이 없습니다.");
        }

        travelJournalRepository.delete(journal);

    }


}
