package it.polimi.mypolihub.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import it.polimi.mypolihub.entity.Professor;

public interface ProfessorRepository extends JpaRepository<Professor, Integer> {

    @Query("select p from Professor p join fetch p.user")
    List<Professor> findAllWithUser();
}
