package it.polimi.mypolihub.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import it.polimi.mypolihub.entity.Course;

public interface CourseRepository extends JpaRepository<Course, Integer> {
    
}
