package com.realtimeradar.service;

import com.realtimeradar.dto.CommentDTO;
import com.realtimeradar.entity.Comment;
import com.realtimeradar.entity.Report;
import com.realtimeradar.repository.CommentRepository;
import com.realtimeradar.repository.ReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final ReportRepository reportRepository;

    @Override
    public void saveComment(CommentDTO dto) {
        Report report = reportRepository.findById(dto.getReportId())
                .orElseThrow(() -> new IllegalArgumentException("해당 제보글이 존재하지 않습니다."));

        Comment parent = null;
        if (dto.getParentId() != null) {
            parent = commentRepository.findById(dto.getParentId())
                    .orElseThrow(() -> new IllegalArgumentException("부모 댓글이 존재하지 않습니다."));
        }

        Comment comment = Comment.builder()
                .author(dto.getAuthor())
                .content(dto.getContent())
                .createdAt(LocalDateTime.now())
                .report(report)
                .parent(parent)
                .build();

        commentRepository.save(comment);

        report.setCommentCount(report.getCommentCount() + 1);
        reportRepository.save(report);
    }

    @Override
    public List<CommentDTO> getCommentsByReportId(Long reportId) {
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new IllegalArgumentException("해당 제보글이 존재하지 않습니다."));

        return commentRepository.findByReportOrderByIdDesc(report).stream()
                .map(comment -> CommentDTO.builder()
                        .id(comment.getId())
                        .author(comment.getAuthor())
                        .content(comment.getContent())
                        .createdAt(comment.getCreatedAt())
                        .reportId(reportId)
                        .parentId(comment.getParent() != null ? comment.getParent().getId() : null) // ✅ 부모 ID
                        .likes(comment.getLikes())
                        .dislikes(comment.getDislikes())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public void increaseLike(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("댓글이 존재하지 않습니다."));
        comment.setLikes(comment.getLikes() + 1);
        commentRepository.save(comment);
    }

    @Override
    public void increaseDislike(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("댓글이 존재하지 않습니다."));
        comment.setDislikes(comment.getDislikes() + 1);
        commentRepository.save(comment);
    }

    @Override
    public CommentDTO getMostLikedComment(Long reportId) {
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new IllegalArgumentException("해당 제보글이 존재하지 않습니다."));

        return commentRepository.findByReportOrderByLikesDesc(report).stream()
                .findFirst()
                .map(comment -> CommentDTO.builder()
                        .id(comment.getId())
                        .author(comment.getAuthor())
                        .content(comment.getContent())
                        .createdAt(comment.getCreatedAt())
                        .reportId(reportId)
                        .parentId(comment.getParent() != null ? comment.getParent().getId() : null)
                        .likes(comment.getLikes())
                        .dislikes(comment.getDislikes())
                        .build())
                .orElse(null);
    }
}
