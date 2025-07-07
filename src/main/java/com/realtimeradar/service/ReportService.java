package com.realtimeradar.service;

import com.realtimeradar.dto.ReportDTO;
import com.realtimeradar.entity.Report;

import java.util.List;

public interface ReportService {
    void saveReport(ReportDTO dto);
    List<ReportDTO> getAllReports();
    ReportDTO getReportById(Long id);
    void deleteReport(Long id);
    void increaseLikes(Long id);
    void increaseDislikes(Long id);
    public Report getPreviousReport(Long id);
    public Report getNextReport(Long id);
}
