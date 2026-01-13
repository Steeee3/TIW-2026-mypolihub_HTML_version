package it.polimi.mypolihub.service;

import java.time.Instant;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.polimi.mypolihub.entity.Exam;
import it.polimi.mypolihub.entity.Report;
import it.polimi.mypolihub.repository.ReportRepository;

@Service
public class ReportService {
    
    @Autowired
    private ReportRepository reportRepository;

    @Transactional
    public Report createReport(Exam exam) {
        if (exam == null) {
            throw new IllegalArgumentException("Exam must exist");
        }

        Report report = new Report();
        report.setExam(exam);
        report.setTimestamp(Instant.now());

        reportRepository.save(report);

        return report;
    }
}
