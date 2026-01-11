package it.polimi.mypolihub.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.polimi.mypolihub.DTO.ExamDTO;
import it.polimi.mypolihub.DTO.RegistrationDTO;
import it.polimi.mypolihub.entity.Course;
import it.polimi.mypolihub.entity.Exam;
import it.polimi.mypolihub.entity.Registration;
import it.polimi.mypolihub.entity.Result;
import it.polimi.mypolihub.entity.Status;
import it.polimi.mypolihub.repository.CourseRepository;
import it.polimi.mypolihub.repository.ExamRepository;
import it.polimi.mypolihub.repository.RegistrationRepository;
import it.polimi.mypolihub.repository.ResultRepository;
import it.polimi.mypolihub.repository.StatusRepository;

@Service
public class ExamService {

    @Autowired
    private ExamRepository examRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private RegistrationRepository registrationRepository;

    @Autowired
    private ResultRepository resultRepository;

    @Autowired
    private StatusRepository statusRepository;

    private static final int STATUS_NON_INSERITO_ID = 1;
    private static final int STATUS_INSERITO_ID = 2;

    private static final Set<Integer> EDITABLE_STATUS_IDS = Set.of(
        STATUS_NON_INSERITO_ID,
        STATUS_INSERITO_ID
    );

    @Transactional
    public void addExamCall(Integer examId, LocalDateTime date) {
        Exam exam = new Exam();

        Course course = courseRepository.findById(examId)
                .orElseThrow(() -> new IllegalArgumentException("Course does not exists"));

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

    @Transactional
    public void setResult(Integer registrationId, Integer resultId) {
        Registration registration = registrationRepository.findById(registrationId)
            .orElseThrow(() -> new IllegalArgumentException("Registration does not exist"));

        int oldStatus = registration.getStatus().getId();
        if (!EDITABLE_STATUS_IDS.contains(oldStatus)) {
            throw new IllegalArgumentException("This registration cannot be edited");
        }
        if (oldStatus == STATUS_NON_INSERITO_ID) {
            Status status = statusRepository.findById(STATUS_INSERITO_ID)
                .orElseThrow(() -> new IllegalStateException("Database does not have row '2': INSERITO"));
            registration.setStatus(status);
        }

        Result result = resultRepository.findById(resultId)
            .orElseThrow(() -> new IllegalArgumentException("Result does not exist"));
        registration.setResult(result);
    }
}
