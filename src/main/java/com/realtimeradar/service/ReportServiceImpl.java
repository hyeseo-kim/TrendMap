package com.realtimeradar.service;

import com.realtimeradar.dto.ReportDTO;
import com.realtimeradar.entity.Report;
import com.realtimeradar.repository.ReportRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final ReportRepository reportRepository;
    private final FileUploadService fileUploadService;

    @Override
    public void saveReport(ReportDTO dto) {
        String filePath = fileUploadService.uploadFile(dto.getImage());

        Report report = Report.builder()
                .nickname(dto.getNickname())
                .title(dto.getTitle())
                .content(dto.getContent())
                .region(dto.getRegion())
                .category(dto.getCategory())
                .imagePath(filePath)
                .views(0) // ✅ 조회수 초기값
                .commentCount(0) // ✅ 댓글 수 초기값
                .build();

        reportRepository.save(report);
    }

    @Override
    public List<ReportDTO> getAllReports() {
        return reportRepository.findAll().stream().map(report -> ReportDTO.builder()
                .id(report.getId())
                .nickname(report.getNickname())
                .title(report.getTitle())
                .content(report.getContent())
                .region(report.getRegion())
                .category(report.getCategory())
                .imagePath(report.getImagePath())
                .views(report.getViews()) // ✅ 조회수 포함
                .commentCount(report.getCommentCount()) // ✅ 댓글 수 포함
                .likes(report.getLikes()) // 추가
                .dislikes(report.getDislikes())
                .build()).collect(Collectors.toList());
    }

    @Transactional
    @Override
    public ReportDTO getReportById(Long id) {
        Report report = reportRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 제보글이 존재하지 않습니다. id=" + id));

        report.setViews(report.getViews() + 1); // 조회수 증가
        reportRepository.save(report);

        return ReportDTO.builder()
                .id(report.getId())
                .nickname(report.getNickname())
                .title(report.getTitle())
                .content(report.getContent())
                .region(report.getRegion())
                .category(report.getCategory())
                .imagePath(report.getImagePath())
                .views(report.getViews())
                .commentCount(report.getCommentCount())
                .createdAt(report.getCreatedAt())
                .likes(report.getLikes()) // 추가
                .dislikes(report.getDislikes())
                .build();
    }

    @Override
    public void deleteReport(Long id) {
        reportRepository.deleteById(id);
    }

    @Transactional
    @Override
    public void increaseLikes(Long id) {
        Report report = reportRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 제보글이 존재하지 않습니다."));
        report.setLikes(report.getLikes() + 1);
        reportRepository.save(report);
    }

    @Transactional
    @Override
    public void increaseDislikes(Long id) {
        Report report = reportRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 제보글이 존재하지 않습니다."));
        report.setDislikes(report.getDislikes() + 1);
        reportRepository.save(report);
    }

    @Override
    public Report getPreviousReport(Long id) {
        return reportRepository.findFirstByIdLessThanOrderByIdDesc(id).orElse(null);
    }

    @Override
    public Report getNextReport(Long id) {
        return reportRepository.findFirstByIdGreaterThanOrderByIdAsc(id).orElse(null);
    }


}
