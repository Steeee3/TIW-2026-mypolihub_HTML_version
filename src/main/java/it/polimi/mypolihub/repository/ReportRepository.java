package it.polimi.mypolihub.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import it.polimi.mypolihub.entity.Report;

public interface ReportRepository extends JpaRepository<Report, Integer> {
    List<Report> findAllByExam_Course_IdAndExam_Course_Professor_IdOrderByExam_DateAsc(Integer courseId, Integer professorId);
}
