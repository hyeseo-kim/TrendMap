package com.realtimeradar.service;

import com.realtimeradar.dto.CommentDTO;

import java.util.List;

public interface CommentService {
    void saveComment(CommentDTO dto);
    List<CommentDTO> getCommentsByReportId(Long reportId);
    void increaseLike(Long commentId);
    void increaseDislike(Long commentId);
    public CommentDTO getMostLikedComment(Long reportId);
}
