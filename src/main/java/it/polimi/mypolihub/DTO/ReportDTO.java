package it.polimi.mypolihub.DTO;

import java.time.Instant;

import it.polimi.mypolihub.entity.Report;

public class ReportDTO {
    private ExamDTO exam;
    private Instant timestamp;

    public ReportDTO(Report report) {
        exam = new ExamDTO(report.getExam());
        timestamp = report.getTimestamp();
    }

    public ExamDTO getExam() {
        return exam;
    }

    public Instant getTimestamp() {
        return timestamp;
    }
}
