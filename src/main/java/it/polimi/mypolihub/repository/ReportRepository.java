package it.polimi.mypolihub.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import it.polimi.mypolihub.entity.Report;

public interface ReportRepository extends JpaRepository<Report, Integer> {
    @Query("""
                select r
                from Report r
                join fetch r.exam e
                join fetch e.course c
                where c.professor.id = :professorId
                order by c.name asc, e.date asc
            """)
    List<Report> findAllByProfessor_IdSorted(@Param("professorId") Integer professorId);
}
