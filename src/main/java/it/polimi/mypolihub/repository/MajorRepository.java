package it.polimi.mypolihub.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import it.polimi.mypolihub.entity.Major;

public interface MajorRepository extends JpaRepository<Major, Integer> {
    boolean existsByNameIgnoreCase(String name);
}
