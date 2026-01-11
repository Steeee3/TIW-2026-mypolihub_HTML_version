package it.polimi.mypolihub.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import it.polimi.mypolihub.entity.Status;

public interface StatusRepository extends JpaRepository<Status, Integer>{
    
}
