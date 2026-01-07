package it.polimi.mypolihub.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.polimi.mypolihub.entity.Major;
import it.polimi.mypolihub.entity.Student;
import it.polimi.mypolihub.entity.User;
import it.polimi.mypolihub.repository.StudentRepository;
import it.polimi.mypolihub.repository.UserRepository;

@Service
public class UserService {
    
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StudentRepository studentRepository;

    public void createStudent(User user, Major major) {
        userRepository.save(user);

        Student student = new Student();
        student.setUser(user);
        student.setMajor(major);
        studentRepository.save(student);
    }
}
