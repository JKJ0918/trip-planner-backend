package com.tripPlanner.project.service.myPage;

import com.tripPlanner.project.dto.myPage.MyJournalsDTO;
import com.tripPlanner.project.repository.travelJournal.TravelJournalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MyJournalsService {

    private final TravelJournalRepository travelJournalRepository;
    public Page<MyJournalsDTO> getMyJournals(Long userId, int page, int size){
        Pageable pageable = PageRequest.of(Math.max(page - 1, 0 ), size);
        return travelJournalRepository
                .findByUserIdOrderByIdDesc(userId, pageable)
                .map(j -> new MyJournalsDTO(j.getId(), j.getTitle(), j.getCreatedAt()));
    }

}
