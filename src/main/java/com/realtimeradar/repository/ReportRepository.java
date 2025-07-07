package com.realtimeradar.repository;

import com.realtimeradar.entity.Report;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReportRepository extends JpaRepository<Report, Long> {
    Optional<Report> findFirstByIdLessThanOrderByIdDesc(Long id);
    Optional<Report> findFirstByIdGreaterThanOrderByIdAsc(Long id);

}
