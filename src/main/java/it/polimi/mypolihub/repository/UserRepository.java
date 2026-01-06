package it.polimi.mypolihub.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import it.polimi.mypolihub.entity.User;

public interface UserRepository extends JpaRepository<User, Integer> {
    long countBy();
    Optional<User> findByEmail(String email);
}
