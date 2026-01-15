package it.polimi.mypolihub.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.polimi.mypolihub.DTO.ExamDTO;
import it.polimi.mypolihub.DTO.RegistrationDTO;
import it.polimi.mypolihub.entity.Course;
import it.polimi.mypolihub.entity.DefaultValues;
import it.polimi.mypolihub.entity.Exam;
import it.polimi.mypolihub.entity.Professor;
import it.polimi.mypolihub.entity.Registration;
import it.polimi.mypolihub.entity.Report;
import it.polimi.mypolihub.entity.Result;
import it.polimi.mypolihub.entity.Status;
import it.polimi.mypolihub.entity.Student;
import it.polimi.mypolihub.repository.CourseRepository;
import it.polimi.mypolihub.repository.ExamRepository;
import it.polimi.mypolihub.repository.RegistrationRepository;
import it.polimi.mypolihub.repository.ResultRepository;
import it.polimi.mypolihub.repository.StatusRepository;
import it.polimi.mypolihub.repository.StudentRepository;

@Service
public class ExamService {

	@Autowired
	private ReportService reportService;

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

	@Autowired
	private StudentRepository studentRepository;

	private static final int STATUS_NON_INSERITO_ID = 1;
	private static final int STATUS_INSERITO_ID = 2;
	private static final int STATUS_PUBBLICATO_ID = 3;
	private static final int STATUS_RIFIUTATO_ID = 4;
	private static final int STATUS_VERBALIZZATO_ID = 5;

	private static final Set<Integer> EDITABLE_STATUS_IDS = Set.of(
			STATUS_NON_INSERITO_ID,
			STATUS_INSERITO_ID);

	private static final Set<Integer> TO_BE_VERBALIZED_STATUS_IDS = Set.of(
			STATUS_PUBBLICATO_ID,
			STATUS_RIFIUTATO_ID);

	private static final Set<Integer> TO_BE_DECLINED_STATUS_IDS = Set.of(
			STATUS_PUBBLICATO_ID);

	private static final Set<Integer> TO_BE_VISUALIZED_STATUS_IDS = Set.of(
			STATUS_PUBBLICATO_ID,
			STATUS_RIFIUTATO_ID,
			STATUS_VERBALIZZATO_ID);

	private static final int RESULT_RIMANDATO_ID = 3;
	private final static int RESULT_18_ID = 5;

	@Transactional
	public void addExamCall(Integer examId, LocalDateTime date) {
		Exam exam = new Exam();

		Course course = courseRepository.findById(examId)
				.orElseThrow(() -> new IllegalArgumentException("Course does not exists"));

		exam.setCourse(course);
		exam.setDate(date);

		examRepository.save(exam);
	}

	@Transactional(readOnly = true)
	public List<ExamDTO> getExamsForCourse(Integer courseId) {
		List<Exam> exams = examRepository.findAllByCourse_IdOrderByDateDesc(courseId);

		return exams.stream()
				.map(exam -> new ExamDTO(exam))
				.toList();
	}

	@Transactional(readOnly = true)
	public Set<Integer> getRegisteredExamIds(Integer studentId, Integer courseId) {
		return registrationRepository.findRegisteredExamIdsByStudentAndCourse(studentId, courseId);
	}

	@Transactional
	public void registerStudentForExam(Integer studentId, Integer examId) {
		Student student = studentRepository.findById(studentId)
				.orElseThrow(() -> new IllegalArgumentException("Student does not exist"));
		Exam exam = examRepository.findById(examId)
				.orElseThrow(() -> new IllegalArgumentException("Exam does not exist"));

		Course course = exam.getCourse();
		if (!course.getStudents().contains(student)) {
			throw new AccessDeniedException("Devi essere iscritto al corso per iscriverti ad un appello");
		}

		Result initialDefaultResult = resultRepository.findById(DefaultValues.RESULT_VUOTO_ID)
				.orElseThrow(() -> new IllegalStateException("Can't find default result value"));
		Status initialDefaultStatus = statusRepository.findById(STATUS_NON_INSERITO_ID)
				.orElseThrow(() -> new IllegalStateException("Can't find default result status"));

		Registration registration = new Registration();
		registration.setStudent(student);
		registration.setExam(exam);
		registration.setResult(initialDefaultResult);
		registration.setStatus(initialDefaultStatus);

		registrationRepository.save(registration);
	}

	@Transactional(readOnly = true)
	public List<RegistrationDTO> getStudentsByExamIdSortedBy(Integer professorId, Integer examId, String sortBy,
			String sortDir) {
		Sort.Direction dir = "desc".equalsIgnoreCase(sortDir) ? Sort.Direction.DESC : Sort.Direction.ASC;
		Sort sort = Sort.by(dir, sortBy);

		Exam exam = examRepository.findById(examId)
				.orElseThrow(() -> new IllegalArgumentException("Exam does not exist"));
		Professor courseProfessor = exam.getCourse().getProfessor();
		if (!professorId.equals(courseProfessor.getId())) {
			throw new AccessDeniedException("Access denied");
		}

		List<Registration> registrations = registrationRepository.findByExam_Id(examId, sort);

		return registrations.stream()
				.map(r -> new RegistrationDTO(r))
				.toList();
	}

	@Transactional
	public void setResult(Integer professorId, Integer registrationId, Integer resultId) {
		Registration registration = registrationRepository.findById(registrationId)
				.orElseThrow(() -> new IllegalArgumentException("L'appello fornito non esiste"));

		Exam exam = registration.getExam();
		Professor courseProfessor = exam.getCourse().getProfessor();
		if (!professorId.equals(courseProfessor.getId())) {
			throw new AccessDeniedException("Assicurati di essere il docente associato al corso.");
		}

		int oldStatus = registration.getStatus().getId();
		if (!EDITABLE_STATUS_IDS.contains(oldStatus)) {
			throw new IllegalArgumentException(
					"Non puoi modificare un appello " + registration.getStatus().getValue());
		}
		if (oldStatus == STATUS_NON_INSERITO_ID) {
			Status status = statusRepository.findById(STATUS_INSERITO_ID)
					.orElseThrow(() -> new IllegalStateException(
							"Database does not have row '2': INSERITO"));
			registration.setStatus(status);
		}

		Result result = resultRepository.findById(resultId)
				.orElseThrow(() -> new IllegalArgumentException("Il voto specificato non esiste"));
		registration.setResult(result);

		registrationRepository.save(registration);
	}

	@Transactional
	public void publishResults(Integer professorId, Integer examId) {
		Exam exam = examRepository.findById(examId)
				.orElseThrow(() -> new IllegalArgumentException("L'esame fornito non esiste"));

		Professor courseProfessor = exam.getCourse().getProfessor();
		if (!professorId.equals(courseProfessor.getId())) {
			throw new AccessDeniedException("Assicurati di essere il docente associato al corso.");
		}

		Status published = statusRepository.findById(STATUS_PUBBLICATO_ID)
				.orElseThrow(() -> new IllegalStateException(
						"Database does not have row '3': PUBBLICATO"));

		int rowsPublished = registrationRepository.publishAllInserted(examId, STATUS_INSERITO_ID, published);
		if (rowsPublished == 0) {
			throw new IllegalArgumentException("Nessun appello da pubblicare");
		}
	}

	@Transactional
	public Integer finalizeResults(Integer professorId, Integer examId) {
		Exam exam = examRepository.findById(examId)
				.orElseThrow(() -> new IllegalArgumentException("L'esame fornito non esiste"));

		Professor courseProfessor = exam.getCourse().getProfessor();
		if (!professorId.equals(courseProfessor.getId())) {
			throw new AccessDeniedException("Assicurati di essere il docente associato al corso.");
		}

		statusRepository.findById(STATUS_VERBALIZZATO_ID)
				.orElseThrow(() -> new IllegalStateException(
						"Database does not have row '5': VERBALIZZATO"));

		resultRepository.findById(RESULT_RIMANDATO_ID)
				.orElseThrow(() -> new IllegalStateException(
						"Database does not have row '3': RIMANDATO"));

		int finalized = registrationRepository.finalizeAll(examId, TO_BE_VERBALIZED_STATUS_IDS,
				STATUS_VERBALIZZATO_ID,
				STATUS_RIFIUTATO_ID, RESULT_RIMANDATO_ID);
		if (finalized > 0) {
			Report report = reportService.createReport(exam);
			registrationRepository.updateReport(examId, STATUS_VERBALIZZATO_ID, report);

			return report.getId();
		} else {
			throw new IllegalArgumentException("Nessun appello da verbalizzare");
		}
	}

	@Transactional(readOnly = true)
	public RegistrationDTO getResultByStudentIdAndExamId(Integer studentId, Integer examId) {
		Registration registration = registrationRepository.findByStudent_IdAndExam_Id(studentId, examId)
				.orElseThrow(() -> new IllegalArgumentException("Nessun iscrizione trovata per l'utente fornito"));

		Status registrationStatus = registration.getStatus();
		if (!TO_BE_VISUALIZED_STATUS_IDS.contains(registrationStatus.getId())) {
			throw new IllegalArgumentException("Il voto non è ancora stato pubblicato");
		}

		return new RegistrationDTO(registration);
	}

	@Transactional
	public void declineExamResult(Integer studentId, Integer examId) {
		Registration registration = registrationRepository.findByStudent_IdAndExam_Id(studentId, examId)
				.orElseThrow(() -> new IllegalArgumentException("Nessun iscrizione trovata per l'utente fornito"));

		Status registrationStatus = registration.getStatus();
		if (!TO_BE_DECLINED_STATUS_IDS.contains(registrationStatus.getId())
				|| registration.getResult().getId() < RESULT_18_ID) {
			throw new IllegalArgumentException("Non puoi rifiutare questo voto");
		}

		Status declined = statusRepository.findById(STATUS_RIFIUTATO_ID)
				.orElseThrow(() -> new IllegalStateException(
						"Database does not have row '4': RIFIUTATO"));

		registration.setStatus(declined);
		registrationRepository.save(registration);
	}
}
