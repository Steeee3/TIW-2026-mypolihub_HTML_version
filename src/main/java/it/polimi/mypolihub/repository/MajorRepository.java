package it.polimi.mypolihub.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import it.polimi.mypolihub.entity.Major;

public interface MajorRepository extends JpaRepository<Major, Integer> {
    boolean existsByNameIgnoreCase(String name);

    @Query("""
                select m
                from Major m
                join fetch m.degreeLevel
                order by m.name asc
            """)
    List<Major> findAllWithDegreeLevel();
}
