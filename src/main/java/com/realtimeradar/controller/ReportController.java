package com.realtimeradar.controller;

import com.realtimeradar.dto.CommentDTO;
import com.realtimeradar.dto.ReportDTO;
import com.realtimeradar.service.CommentService;
import com.realtimeradar.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@RequestMapping("/report")
public class ReportController {

    private final ReportService reportService;
    private final CommentService commentService;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("reports", reportService.getAllReports());
        return "report/report-list";
    }

    @GetMapping("/write")
    public String writeForm(Model model) {
        model.addAttribute("reportDTO", new ReportDTO());
        return "report/report-write";
    }

    @PostMapping("/write")
    public String submit(@ModelAttribute ReportDTO dto) {
        reportService.saveReport(dto);
        return "redirect:/report";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        reportService.deleteReport(id);
        return "redirect:/report";
    }

    @GetMapping("/detail/{id}")
    public String detail(@PathVariable Long id, Model model) {
        try {
            List<CommentDTO> comments = commentService.getCommentsByReportId(id);

            Map<Long, Integer> replyCountMap = new HashMap<>();
            for (CommentDTO comment : comments) {
                if (comment.getParentId() != null) {
                    replyCountMap.put(
                            comment.getParentId(),
                            replyCountMap.getOrDefault(comment.getParentId(), 0) + 1
                    );
                }
            }

            ReportDTO report = reportService.getReportById(id);
            if (report.getContent() != null) {
                report.setContent(report.getContent()
                        .replace("\r\n", "<br/>")
                        .replace("\n", "<br/>"));
            }

            model.addAttribute("report", report);
            model.addAttribute("comments", comments);
            model.addAttribute("hotComment", commentService.getMostLikedComment(id));
            model.addAttribute("previousReport", reportService.getPreviousReport(id));
            model.addAttribute("nextReport", reportService.getNextReport(id));
            model.addAttribute("replyCountMap", replyCountMap);

            return "report/report-detail";

        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "error/404";
        }
    }

    @PostMapping("/like/{id}")
    public String likeReport(@PathVariable Long id) {
        reportService.increaseLikes(id);
        return "redirect:/report/detail/" + id;
    }

    @PostMapping("/dislike/{id}")
    public String dislike(@PathVariable Long id) {
        reportService.increaseDislikes(id);
        return "redirect:/report/detail/" + id;
    }

}
