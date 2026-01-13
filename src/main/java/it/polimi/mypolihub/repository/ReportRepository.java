package it.polimi.mypolihub.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import it.polimi.mypolihub.entity.Report;

public interface ReportRepository extends JpaRepository<Report, Integer> {
    
}
