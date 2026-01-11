package it.polimi.mypolihub.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import it.polimi.mypolihub.entity.Result;

public interface ResultRepository extends JpaRepository<Result, Integer> {
    List<Result> findAllByOrderByIdAsc();
}
