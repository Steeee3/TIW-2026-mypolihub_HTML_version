package it.polimi.mypolihub.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import it.polimi.mypolihub.entity.Professor;

public interface ProfessorRepository extends JpaRepository<Professor, Integer> {
    
}
