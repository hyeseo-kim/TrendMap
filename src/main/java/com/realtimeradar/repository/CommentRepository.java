package com.realtimeradar.repository;

import com.realtimeradar.entity.Comment;
import com.realtimeradar.entity.Report;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByReportOrderByIdDesc(Report report);
    List<Comment> findByReportOrderByLikesDesc(Report report);
}
