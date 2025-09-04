package com.tripPlanner.project.service.myPage;

import com.tripPlanner.project.dto.myPage.MyJournalsDTO;
import com.tripPlanner.project.entity.travelJournal.TravelJournalEntity;
import com.tripPlanner.project.repository.travelJournal.JournalLikeRepository;
import com.tripPlanner.project.repository.travelJournal.TravelJournalRepository;
import com.tripPlanner.project.repository.travelJournal.JournalLikeCount;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MyJournalsService {

    private final TravelJournalRepository travelJournalRepository;
    private final JournalLikeRepository journalLikeRepository;

    public Page<MyJournalsDTO> getMyJournals(Long userId, int page, int size){
        Pageable pageable = PageRequest.of(Math.max(page - 1, 0 ), size);

        // 1) 내 글 페이지 가져오기
        Page<TravelJournalEntity> pageEntites =
                travelJournalRepository.findByUserIdOrderByIdDesc(userId, pageable);

        // 2) 현재 페이지 글 ID 수집
        List<Long> ids = pageEntites.getContent().stream()
                .map(TravelJournalEntity::getId)
                .toList();

        // 3) 좋아요 수 일괄 조회 → Map<Long journalId, Long cnt>
        Map<Long, Long> likeCountMap = ids.isEmpty()
                ? Map.of() // true
                : journalLikeRepository.countByJournalIds(ids).stream()
                .collect(Collectors.toMap(
                        JournalLikeCount::getJournalId,  // 인터페이스 메서드 참조
                        JournalLikeCount::getCnt         // 인터페이스 메서드 참조
                ));


        return travelJournalRepository
                .findByUserIdOrderByIdDesc(userId, pageable)
                .map(j -> new MyJournalsDTO(j.getId(), j.getTitle(), j.getCreatedAt(), likeCountMap.getOrDefault(j.getId(), 0L),j.getViews()));
    }

}
