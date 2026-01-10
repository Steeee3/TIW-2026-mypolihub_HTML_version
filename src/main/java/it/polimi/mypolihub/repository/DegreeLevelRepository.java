package it.polimi.mypolihub.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import it.polimi.mypolihub.entity.DegreeLevel;

public interface DegreeLevelRepository extends JpaRepository<DegreeLevel, Integer> {
    
}
