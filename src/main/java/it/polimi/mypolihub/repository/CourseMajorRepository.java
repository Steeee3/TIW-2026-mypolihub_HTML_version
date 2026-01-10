package it.polimi.mypolihub.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import it.polimi.mypolihub.entity.CourseMajor;

public interface CourseMajorRepository extends JpaRepository<CourseMajor, Integer> {
    
}
