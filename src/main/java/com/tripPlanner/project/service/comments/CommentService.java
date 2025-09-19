package com.tripPlanner.project.service.comments;

import com.tripPlanner.project.dto.comments.CommentRequestDTO;
import com.tripPlanner.project.dto.comments.CommentResponseDTO;
import com.tripPlanner.project.entity.UserEntity;
import com.tripPlanner.project.entity.comments.CommentEntity;
import com.tripPlanner.project.entity.comments.CommentLikeEntity;
import com.tripPlanner.project.entity.notification.NotificationEntity;
import com.tripPlanner.project.entity.travelJournal.TravelJournalEntity;
import com.tripPlanner.project.repository.UserRepository;
import com.tripPlanner.project.repository.comments.CommentLikeRepository;
import com.tripPlanner.project.repository.comments.CommentRepository;
import com.tripPlanner.project.repository.travelJournal.TravelJournalRepository;
import com.tripPlanner.project.service.notification.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.Collections;
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
    private final NotificationService notificationService;

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

        // 댓글 작성 알람
        // 1) 방금 저장한 댓글 엔티티: saved
        CommentEntity saved = commentRepository.save(commentEntity);

        // 2) 알람 받을 사람(게시글 작성자) 찾기 (recipientId)
        Long recipientId = journal.getUser().getId();

        // 3) 댓글 작성자의 닉네임
        String actorNickname = user.getNickname();

        // 4) 자기 자신에게는 알림 보내지 않기
        if (!recipientId.equals(userId)) {
            notificationService.createAndSend(
                    recipientId,                     // 받는 사람
                    userId,                          // 행동 주체(댓글 단 사람)
                    NotificationEntity.Type.COMMENT, // 알림 타입
                    journalId,                       // postId에 해당(여행일지 id)
                    saved.getId(),                   // commentId
                    actorNickname + "님이 댓글을 남겼습니다.",
                    // 프론트 라우팅에 맞춰 링크 수정하세요.
                    // (당신 프로젝트가 /posts/[id]면 "/posts/" + journalId)
                    // (만약 /journals/[id] 라우팅이면 "/journals/" + journalId)
                    "/posts/" + journalId + "#comment-" + saved.getId()
            );
        }


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

        final String DEFAULT_AVATAR = "/uploads/basic_profile.png";
        return commentPage.map(c -> CommentResponseDTO.builder()
                .id(c.getId())
                .content(c.getContent())
                .writerName(c.getUser().getNickname())
                .writerId(c.getUser().getId())
                .avatarUrl(               // ← 이 줄만 추가
                        Optional.ofNullable(c.getUser().getAvatarUrl()).orElse(DEFAULT_AVATAR)
                )
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
    public Page<CommentResponseDTO> getRepliesPage(Long parentId, Long userId, Pageable pageable){

        // 1) 페이지 단위로 대댓글 조회
        Page<CommentEntity> page = commentRepository.findByParentId(parentId, pageable);

        // 2) 로그인 유저(옵션) - 없으면 LikedByMe 는 false, author 판정은 불가
        UserEntity loginUser = null;
        if (userId != null){
            loginUser = userRepository.findById(userId).orElse(null);
        }

        // 3) 좋아요 정보(본인 확인) - 현재 페이지에 한정해서 조회
        Set<Long> likedCommentIds = Collections.emptySet();
        if(loginUser != null && !page.getContent().isEmpty()){
            List<CommentLikeEntity> liked = commentLikeRepository.findByUserAndCommentIn(
                    loginUser,
                    page.getContent()
            );
            likedCommentIds = liked.stream()
                    .map(l -> l.getComment().getId())
                    .collect(Collectors.toSet());
        }

        // 4) DTO 매핑
        // loginUser, likedCommentIds 를 final 변수로 고정
        final UserEntity finalLoginUser = loginUser;
        final Set<Long> finalLikedCommentIds = likedCommentIds;

        return page.map(c -> CommentResponseDTO.builder()
                .id(c.getId())
                .content(c.getContent())
                .writerName(c.getUser().getNickname())
                .writerId(c.getUser().getId())
                .createdAt(c.getCreatedAt())
                .parentId(c.getParent() != null ? c.getParent().getId() : null)
                .replyCount(c.getChildren() != null ? c.getChildren().size() : 0)
                .edited(c.isEdited())
                .isAuthor(finalLoginUser != null && c.getUser().getId().equals(finalLoginUser.getId()))
                .likeCount(c.getLikes() != null ? c.getLikes().size() : 0)
                .likedByMe(finalLikedCommentIds.contains(c.getId()))
                .avatarUrl(c.getUser().getAvatarUrl())
                .build()
        );

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

    // 댓글 저장 성공 후
    public void afterCreateComment(Long postId, Long commentId, Long postAuthorId, Long actorId, String actorNickname) {
        notificationService.createAndSend(
                postAuthorId, actorId,
                NotificationEntity.Type.COMMENT,
                postId, commentId,
                actorNickname + "님이 댓글을 남겼습니다.",
                "/posts/" + postId + "#comment-" + commentId
        );
    }

    // 좋아요 저장 성공 후
    public void afterLikePost(Long postId, Long postAuthorId, Long actorId, String actorNickname) {
        notificationService.createAndSend(
                postAuthorId, actorId,
                NotificationEntity.Type.LIKE,
                postId, null,
                actorNickname + "님이 좋아요를 눌렀습니다.",
                "/posts/" + postId
        );
    }

    
}
