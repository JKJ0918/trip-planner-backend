package com.tripPlanner.project.repository.travelJournal;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface JournalLikeCount {
    Long getJournalId();
    Long getCnt();

}
