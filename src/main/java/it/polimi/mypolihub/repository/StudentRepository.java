package it.polimi.mypolihub.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import it.polimi.mypolihub.entity.Student;

public interface StudentRepository extends JpaRepository<Student, Integer> {
    
}
