package it.polimi.mypolihub.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import it.polimi.mypolihub.entity.Exam;

public interface ExamRepository extends JpaRepository<Exam, Integer> {
    
}
