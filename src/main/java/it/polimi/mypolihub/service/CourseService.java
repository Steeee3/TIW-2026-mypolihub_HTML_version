package it.polimi.mypolihub.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.polimi.mypolihub.DTO.CourseDTO;
import it.polimi.mypolihub.entity.Course;
import it.polimi.mypolihub.entity.Major;
import it.polimi.mypolihub.entity.Professor;
import it.polimi.mypolihub.repository.CourseRepository;
import it.polimi.mypolihub.repository.MajorRepository;
import it.polimi.mypolihub.repository.ProfessorRepository;

@Service
public class CourseService {

    @Autowired
    private MajorRepository majorRepository;

    @Autowired
    private ProfessorRepository professorRepository;

    @Autowired
    private CourseRepository courseRepository;
    
    @Transactional
    public void createCourse(String rawName, Integer cfu, Integer majorId, Integer professorId) {
        String name = rawName == null ? "" : rawName.trim().replaceAll("\\s+", " ");

        if (name.isBlank()) throw new IllegalArgumentException("Course name is blank");

        Major major = majorRepository.findById(majorId).orElseThrow(() -> new IllegalArgumentException("Major does not exists"));
        Professor professor = professorRepository.findById(professorId).orElseThrow(() -> new IllegalArgumentException("Professor does not exists"));

        Course course = new Course();
        course.setName(name);
        course.setCfu(cfu);
        course.setMajor(major);
        course.setProfessor(professor);

        courseRepository.save(course);
    }

    @Transactional
    public List<CourseDTO> findCoursesByStudentId(Integer studentId) {
        List<CourseDTO> coursesDTO = new ArrayList<>();

        List<Course> courses = courseRepository.findByStudents_IdOrderByNameDesc(studentId);
        for (Course course : courses) {
            coursesDTO.add(new CourseDTO(course));
        }

        return coursesDTO;
    }

    @Transactional
    public List<CourseDTO> findCoursesByProfessorId(Integer professorId) {
        List<CourseDTO> coursesDTO = new ArrayList<>();

        List<Course> courses = courseRepository.findByProfessor_IdOrderByNameDesc(professorId);
        for (Course course : courses) {
            coursesDTO.add(new CourseDTO(course));
        }

        return coursesDTO;
    }
}
