package it.polimi.mypolihub.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;

@Entity
@Table(
    name = "exams",
    indexes = {
        @Index(name = "FK_exams_reports", columnList = "report_id"),
        @Index(name = "FK_exams_courses", columnList = "course_id")
    }
)
public class Exam {
    
}
