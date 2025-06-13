package com.tripPlanner.project.service.comments;

import com.tripPlanner.project.dto.comments.CommentRequestDTO;
import com.tripPlanner.project.dto.comments.CommentResponseDTO;
import com.tripPlanner.project.entity.UserEntity;
import com.tripPlanner.project.entity.comments.CommentEntity;
import com.tripPlanner.project.entity.comments.CommentLikeEntity;
import com.tripPlanner.project.entity.travelJournal.TravelJournalEntity;
import com.tripPlanner.project.repository.UserRepository;
import com.tripPlanner.project.repository.comments.CommentLikeRepository;
import com.tripPlanner.project.repository.comments.CommentRepository;
import com.tripPlanner.project.repository.travelJournal.TravelJournalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final CommentLikeRepository commentLikeRepository;
    private final UserRepository userRepository;
    private final TravelJournalRepository travelJournalRepository;

    // 댓글 작성
    public void addComment(Long journalId, Long userId, CommentRequestDTO dto){

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자 입니다."));

        TravelJournalEntity journal = travelJournalRepository.findById(journalId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시글 입니다."));

        CommentEntity parent = null;
        if(dto.getParentId() != null){
            parent = commentRepository.findById(dto.getParentId())
                    .orElseThrow(() -> new IllegalArgumentException("부모 댓글 없음"));
        }

        CommentEntity commentEntity = CommentEntity.builder()
                .user(user)
                .travelJournal(journal)
                .parent(parent)
                .content(dto.getContent())
                .build();

        commentRepository.save(commentEntity);

    }


    // 댓글 가져오기 - 무한 스크롤 + 정렬 기능 추가
    public Page<CommentResponseDTO> getTopLevelComments(Long journalId, Long userId, int page, int size, String sort) {
        Pageable pageable = PageRequest.of(page, size);
        Page<CommentEntity> commentPage;

        if ("popular".equals(sort)) {
            commentPage = commentRepository.findTopLevelCommentsByJournalIdOrderByLikesDesc(journalId, pageable);
        } else {
            commentPage = commentRepository.findTopLevelCommentsByJournalIdOrderByCreatedAtDesc(journalId, pageable);
        }

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저 없음"));

        List<CommentLikeEntity> liked = commentLikeRepository.findByUserAndCommentIn(user, commentPage.getContent());
        Set<Long> likedCommentIds = liked.stream()
                .map(like -> like.getComment().getId())
                .collect(Collectors.toSet());

        return commentPage.map(c -> CommentResponseDTO.builder()
                .id(c.getId())
                .content(c.getContent())
                .writerName(c.getUser().getNickname())
                .createdAt(c.getCreatedAt())
                .parentId(null)
                .edited(c.isEdited())
                .replyCount(c.getChildren().size())
                .isAuthor(c.getUser().getId().equals(userId))
                .likeCount(c.getLikes().size())
                .likedByMe(likedCommentIds.contains(c.getId()))
                .build());
    }



    // 대댓글 가져오기
    public List<CommentResponseDTO> getReplies(Long parentId, Long userId) {
        List<CommentEntity> replies = commentRepository.findByParentIdOrderByCreatedAtAsc(parentId);

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저 없음"));

        List<CommentLikeEntity> liked = commentLikeRepository.findByUserAndCommentIn(user, replies);
        Set<Long> likedCommentIds = liked.stream()
                .map(like -> like.getComment().getId())
                .collect(Collectors.toSet());

        return replies.stream().map(c -> CommentResponseDTO.builder()
                .id(c.getId())
                .content(c.getContent())
                .writerName(c.getUser().getNickname())
                .createdAt(c.getCreatedAt())
                .parentId(c.getParent().getId())
                .edited(c.isEdited())
                .isAuthor(c.getUser().getId().equals(userId))
                .likeCount(c.getLikes().size())
                .likedByMe(likedCommentIds.contains(c.getId()))
                .build()
        ).toList();
    }



    // 댓글 수정
    public void updateComment(Long commentId, Long userId, CommentRequestDTO dto){

        CommentEntity comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("댓글 없음"));

        if(!comment.getUser().getId().equals(userId)){
            throw new IllegalArgumentException("수정 권한 없음");
        }

        comment.setContent(dto.getContent());
        comment.setEdited(true);
        commentRepository.save(comment);
    }


    // 댓글 삭제
    public void deleteComment (Long commentId, Long userId){
        System.out.println("댓글 삭제 메소드 작동확인");
        CommentEntity comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("댓글 없음"));

        if (!comment.getUser().getId().equals(userId)){
            throw new IllegalArgumentException("댓글 삭제 권한 없음");
        }

        commentRepository.delete(comment);
    }
    
    // 좋아요
    public boolean toggleLike(Long commentId, Long userId){
        CommentEntity comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("댓글 없음"));
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저 없음"));

        Optional<CommentLikeEntity> existing = commentLikeRepository.findByUserAndComment(user, comment);
     
        if(existing.isPresent()){

            commentLikeRepository.delete(existing.get());
            return false; // 좋아요 취소됨
        } else {
            CommentLikeEntity like = new CommentLikeEntity();
            like.setUser(user);
            like.setComment(comment);
            commentLikeRepository.save(like);

            return true; // 좋아요 추가됨
        }


    }

    
}
