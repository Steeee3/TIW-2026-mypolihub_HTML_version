package it.polimi.mypolihub.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.polimi.mypolihub.DTO.ExamDTO;
import it.polimi.mypolihub.DTO.RegistrationDTO;
import it.polimi.mypolihub.entity.Course;
import it.polimi.mypolihub.entity.Exam;
import it.polimi.mypolihub.entity.Registration;
import it.polimi.mypolihub.repository.CourseRepository;
import it.polimi.mypolihub.repository.ExamRepository;
import it.polimi.mypolihub.repository.RegistrationRepository;

@Service
public class ExamService {

    @Autowired
    private ExamRepository examRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private RegistrationRepository registrationRepository;

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
        List<Exam> exams = examRepository.findAllByCourse_IdOrderByDateDesc(courseId);

        return exams.stream()
            .map(exam -> new ExamDTO(exam))
            .toList();
    }

    @Transactional
    public List<RegistrationDTO> getStudentsByExamIdSortedBy(Integer examId, String sortBy, String sortDir) {
        Sort.Direction dir = "desc".equalsIgnoreCase(sortDir) ? Sort.Direction.DESC : Sort.Direction.ASC;
        Sort sort = Sort.by(dir, sortBy);
        List<Registration> registrations = registrationRepository.findByExam_Id(examId, sort);

        return registrations.stream()
            .map(r -> new RegistrationDTO(r))
            .toList();
    }
}
