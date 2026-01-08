package it.polimi.mypolihub.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.polimi.mypolihub.DTO.ExamDTO;
import it.polimi.mypolihub.entity.Course;
import it.polimi.mypolihub.entity.Exam;
import it.polimi.mypolihub.repository.CourseRepository;
import it.polimi.mypolihub.repository.ExamRepository;

@Service
public class ExamService {

    @Autowired
    private ExamRepository examRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Transactional
    public void addExamCall(Integer examId, LocalDateTime date) {
        Exam exam = new Exam();

        Course course = courseRepository.findById(examId).orElseThrow(() -> new IllegalArgumentException("Course does not exists"));

        exam.setCourse(course);
        exam.setDate(date);

        examRepository.save(exam);
    }

    @Transactional
    public List<ExamDTO> getExamsForCourse(Integer courseId) {
        List<Exam> exams = examRepository.findAllByCourse_IdOrderByDateAsc(courseId);

        return exams.stream()
            .map(exam -> new ExamDTO(exam))
            .toList();
    }
}
