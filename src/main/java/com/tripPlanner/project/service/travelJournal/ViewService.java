package com.tripPlanner.project.service.travelJournal;

import com.tripPlanner.project.component.ViewProperties;
import com.tripPlanner.project.repository.travelJournal.TravelJournalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class ViewService {

    private final StringRedisTemplate redis;
    private final TravelJournalRepository travelJournalRepository;
    private final ViewProperties viewProperties; // cooldown 시간

    @Transactional
    public void recordView(long postId, long userId) {
        String viewerKey = "u:" + userId;
        String redisKey  = "view:post:" + postId + ":" + viewerKey;

        Duration ttl = Duration.ofHours(viewProperties.getCooldownHours()); // 작동 확인 필요

        Boolean firstSeen = redis.opsForValue().setIfAbsent(redisKey, "1", ttl);
        if (Boolean.TRUE.equals(firstSeen)) {
            int updated = travelJournalRepository.incrementViews(postId);
            if (updated == 0) {
                redis.delete(redisKey);
                throw new IllegalArgumentException("Post not found: " + postId);
            }
        }
    }

}
