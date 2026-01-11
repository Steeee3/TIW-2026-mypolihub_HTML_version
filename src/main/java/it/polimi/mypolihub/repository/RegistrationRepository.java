package it.polimi.mypolihub.repository;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import it.polimi.mypolihub.entity.Registration;

public interface RegistrationRepository extends JpaRepository<Registration, Integer> {
    List<Registration> findByExam_Id(Integer examId, Sort sort);
}
