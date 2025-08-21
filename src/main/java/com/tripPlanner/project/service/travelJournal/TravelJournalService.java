package com.tripPlanner.project.service.travelJournal;

import com.tripPlanner.project.dto.travelJournal.*;
import com.tripPlanner.project.entity.travelJournal.JournalEntity;
import com.tripPlanner.project.entity.travelJournal.PhotoEntity;
import com.tripPlanner.project.entity.travelJournal.PinEntity;
import com.tripPlanner.project.entity.travelJournal.TravelJournalEntity;
import com.tripPlanner.project.entity.UserEntity;
import com.tripPlanner.project.repository.travelJournal.JournalLikeCount;
import com.tripPlanner.project.repository.travelJournal.JournalLikeRepository;
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
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class TravelJournalService {

    private final UserRepository userRepository;
    private final TravelJournalRepository travelJournalRepository;
    private final JournalLikeRepository journalLikeRepository;


    // 게시글 저장
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
                .description(requestDTO.getDescription())
                .isPublic(requestDTO.getIsPublic())
                .useFlight(requestDTO.getUseFlight())
                .flightDepartureAirline(requestDTO.getFlightDepartureAirline())
                .flightDepartureName(requestDTO.getFlightDepartureName())
                .flightDepartureTime(requestDTO.getFlightDepartureTime())
                .flightDepartureAirport(requestDTO.getFlightDepartureAirport())
                .flightArrivalAirport(requestDTO.getFlightArrivalAirport())
                .flightReturnAirline(requestDTO.getFlightReturnAirline())
                .flightReturnName(requestDTO.getFlightReturnName())
                .flightReturnTime(requestDTO.getFlightReturnTime())
                .flightReturnDepartureAirport(requestDTO.getFlightReturnDepartureAirport())
                .flightReturnArrivalAirport(requestDTO.getFlightReturnArrivalAirport())
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

    // 게시글 목록
    public Page<TravelPostSummaryDTO> getPublicJournals(int page, int size, String keyword) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        Page<TravelJournalEntity> journals =
                (keyword != null && !keyword.isBlank())
                        ? travelJournalRepository.searchPublicByKeyword(keyword, pageable)
                        : travelJournalRepository.findByIsPublicTrue(pageable);

        // 1) 현재 페이지의 게시글 ID 수집
        List<Long> ids = journals.getContent().stream()
                .map(TravelJournalEntity::getId)
                .toList();

        // 2) 좋아요 수 일괄 조회 → Map으로 변환
        Map<Long, Long> likeCountMap = ids.isEmpty()
                ? Collections.emptyMap()
                : journalLikeRepository.countByJournalIds(ids).stream()
                .collect(Collectors.toMap(
                        JournalLikeCount::getJournalId,
                        JournalLikeCount::getCnt
                ));

        // 3) DTO 변환 (likeCount 합쳐 넣기)
        return journals.map(journal -> new TravelPostSummaryDTO(
                journal.getId(),
                journal.getTitle(),
                journal.getLocationSummary(),
                extractThumbnail(journal),
                journal.getUser().getNickname(),
                journal.getCreatedAt(),
                likeCountMap.getOrDefault(journal.getId(), 0L)  // 👈 추가
        ));
    }


    // 상세페이지_특정게시물 가져오기
    public TravelPostDetailDTO getPostDetailById(Long travelJournalId, Long userId) {
        TravelJournalEntity journal = travelJournalRepository.findById(travelJournalId)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글이 존재하지 않습니다."));

        long likeCount = journalLikeRepository.countByTravelJournalLikeEntity_Id(travelJournalId);
        boolean likedByMe = (userId != null) &&
                journalLikeRepository.existsByTravelJournalLikeEntity_IdAndUserId(travelJournalId, userId);

        List<PinDTO> pins = journal.getPinEntities().stream().map(pin -> new PinDTO(
                pin.getLat(),
                pin.getLng(),
                pin.getName(),
                pin.getCategory(),
                pin.getAddress(),
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

        return TravelPostDetailDTO.builder()
                .id(journal.getId())
                .title(journal.getTitle())
                .locationSummary(journal.getLocationSummary())
                .description(journal.getDescription())
                .useFlight(journal.getUseFlight())
                .flightDepartureAirline(journal.getFlightDepartureAirline())
                .flightDepartureName(journal.getFlightDepartureName())
                .flightDepartureTime(journal.getFlightDepartureTime())
                .flightDepartureAirport(journal.getFlightDepartureAirport())
                .flightArrivalAirport(journal.getFlightArrivalAirport())
                .flightReturnAirline(journal.getFlightReturnAirline())
                .flightReturnName(journal.getFlightReturnName())
                .flightReturnTime(journal.getFlightReturnTime())
                .flightReturnDepartureAirport(journal.getFlightReturnDepartureAirport())
                .flightReturnArrivalAirport(journal.getFlightReturnArrivalAirport())
                .travelTrans(journal.getTravelTrans())
                .totalBudget(journal.getTotalBudget())
                .travelTheme(journal.getTravelTheme())
                .review(journal.getReview())
                .isAfterTravel(journal.getIsAfterTravel())
                .dateRange(new DateRangeDTO(
                        journal.getStartDate().toString(),
                        journal.getEndDate().toString()
                ))
                .thumbnailUrl(thumbnailUrl)   // 썸네일 동적 추출
                .authorNickname(journal.getUser().getNickname()) // 작성자 닉네임
                .pins(pins)                   // 지도 핀 목록
                .itinerary(itinerary)         // 일정 목록
                .likeCount(likeCount)         // 👍 좋아요 수
                .likedByMe(likedByMe)         // 👍 내가 좋아요 눌렀는지
                .build();
    }


    // 썸네일 추출 - posts 게시물 리스트에 PostCard.tsx의 썸네일
    private String extractThumbnail(TravelJournalEntity journal) {
        if (journal.getJournalEntities() != null && !journal.getJournalEntities().isEmpty()) {
            for (JournalEntity entry : journal.getJournalEntities()) {
                if (entry.getPhotos() != null && !entry.getPhotos().isEmpty()) {
                    return entry.getPhotos().get(0).getUrl(); // 가장 먼저 발견된 사진
                }
            }
        }
        return "https://your-default-thumbnail.com/default.jpg"; // 썸네일 없을 경우 기본값
    }

    // 게시글 본인 확인
    @Transactional(readOnly = true)
    public Optional<MeResponse> getMe(Long userId) {
        return userRepository.findById(userId)
                .map(user -> new MeResponse(user.getId(), user.getNickname()));
    }

    // 게시글 본인 확인(필요한 경우 별도 메서드로 유지해도 됨)
    @Transactional(readOnly = true)
    public String getNicknameByUserId(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."))
                .getNickname();
    }

    public record MeResponse(Long id, String nickname) {}

    // 게시글 수정
    @Transactional
    public void updatePost(Long id, PostUpdateDTO dto, Long userId) {
        TravelJournalEntity journal = travelJournalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("게시글이 존재하지 않습니다."));

        if (!journal.getUser().getId().equals(userId)) {
            throw new RuntimeException("작성자만 수정할 수 있습니다.");
        }

        // 1) 기본 정보 업데이트 (필요한 필드 추가)
        journal.setTitle(dto.getTitle());
        journal.setLocationSummary(dto.getLocationSummary());
        journal.setStartDate(LocalDate.parse(dto.getDateRange().getStartDate())); // "YYYY-MM-DD"
        journal.setEndDate(LocalDate.parse(dto.getDateRange().getEndDate()));

        // 선택: 아래 필드들도 DTO에 있다면 함께 세팅
        // journal.setDescription(dto.getDescription());
        // journal.setIsPublic(dto.getIsPublic());
        // journal.setUseFlight(dto.getUseFlight());
        // journal.setFlightDepartureAirline(dto.getFlightDepartureAirline());
        // ... 나머지 flight/transport/budget/theme/review/isAfterTravel 등

        // 2) 핀 갱신 (전체 교체 전략: orphanRemoval=true이므로 DB 정리 안전)
        journal.getPinEntities().clear();
        if (dto.getPins() != null) {
            for (PinDTO pinDTO : dto.getPins()) {
                PinEntity pin = new PinEntity();
                pin.setLat(pinDTO.getLat());
                pin.setLng(pinDTO.getLng());
                pin.setName(pinDTO.getName());
                pin.setAddress(pinDTO.getAddress());
                pin.setCategory(pinDTO.getCategory());

                // ← 프론트가 준 세부 필드 반영
                pin.setMinCost(pinDTO.getMinCost());
                pin.setMaxCost(pinDTO.getMaxCost());
                pin.setCurrency(pinDTO.getCurrency());
                pin.setOpenTime(pinDTO.getOpenTime());
                pin.setCloseTime(pinDTO.getCloseTime());
                pin.setDescription(pinDTO.getDescription());

                // 이미지 URL들 저장 (ElementCollection)
                pin.setImages(pinDTO.getImages());

                pin.setTravelJournalPinEntity(journal);
                journal.getPinEntities().add(pin);
            }
        }

        // 3) 일일 일정 갱신 (전체 교체 전략)
        journal.getJournalEntities().clear();
        if (dto.getItinerary() != null) {
            for (JournalUpdateDTO entry : dto.getItinerary()) {
                JournalEntity journalEntity = new JournalEntity();
                journalEntity.setTitle(entry.getTitle());
                journalEntity.setDescription(entry.getContent());
                journalEntity.setDate(entry.getDate()); // DTO가 LocalDate 이므로 그대로 세팅

                journalEntity.setTravelJournalEntity(journal);

                // 사진 URL → PhotoEntity로 매핑
                List<PhotoEntity> photos = (entry.getImages() == null ? List.<String>of() : entry.getImages())
                        .stream()
                        .map(url -> PhotoEntity.builder()
                                .url(url)
                                .journalEntity(journalEntity)
                                .build())
                        .toList();

                journalEntity.setPhotos(photos);
                journal.getJournalEntities().add(journalEntity);
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
