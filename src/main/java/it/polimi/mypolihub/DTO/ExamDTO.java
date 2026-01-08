package it.polimi.mypolihub.DTO;

import java.time.LocalDateTime;

import it.polimi.mypolihub.entity.Exam;
import it.polimi.mypolihub.entity.Report;

public class ExamDTO {
    private Integer id;
    private LocalDateTime date;
    private CourseDTO course;
    private ReportDTO report;

    public ExamDTO(Exam exam) {
        id = exam.getId();
        date = exam.getDate();
        course = new CourseDTO(exam.getCourse());
        
        Report reportObject = exam.getReport();
        if (reportObject != null) {
            report = new ReportDTO(reportObject);
        }
    }

    public Integer getId() {
        return id;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public CourseDTO getCourse() {
        return course;
    }

    public ReportDTO getReport() {
        return report;
    }
}
