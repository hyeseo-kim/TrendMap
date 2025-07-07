package com.realtimeradar.controller;

import com.realtimeradar.dto.CommentDTO;
import com.realtimeradar.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/comment")
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/add")
    public String addComment(@ModelAttribute CommentDTO dto) {
        commentService.saveComment(dto);
        return "redirect:/report/detail/" + dto.getReportId();
    }

    @PostMapping("/like/{id}")
    public String likeComment(@PathVariable Long id, @RequestParam Long reportId) {
        commentService.increaseLike(id);
        return "redirect:/report/detail/" + reportId;
    }

    @PostMapping("/dislike/{id}")
    public String dislikeComment(@PathVariable Long id, @RequestParam Long reportId) {
        commentService.increaseDislike(id);
        return "redirect:/report/detail/" + reportId;
    }
}
