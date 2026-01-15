package it.polimi.mypolihub.service;

import java.time.Instant;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.polimi.mypolihub.DTO.ReportDTO;
import it.polimi.mypolihub.entity.Course;
import it.polimi.mypolihub.entity.Exam;
import it.polimi.mypolihub.entity.Professor;
import it.polimi.mypolihub.entity.Registration;
import it.polimi.mypolihub.entity.Report;
import it.polimi.mypolihub.repository.RegistrationRepository;
import it.polimi.mypolihub.repository.ReportRepository;

@Service
public class ReportService {

    @Autowired
    private ReportRepository reportRepository;

    @Autowired
    private RegistrationRepository registrationRepository;

    @Transactional
    public Report createReport(Exam exam) {
        if (exam == null) {
            throw new IllegalArgumentException("Exam must exist");
        }

        Report report = new Report();
        report.setExam(exam);
        report.setTimestamp(Instant.now());

        reportRepository.save(report);

        return report;
    }

    @Transactional(readOnly = true)
    public ReportDTO getReportByIdSortedBy(Integer professorId, Integer reportId, String sortBy,
            String sortDir) {
        Sort.Direction dir = "desc".equalsIgnoreCase(sortDir) ? Sort.Direction.DESC : Sort.Direction.ASC;
        Sort sort = Sort.by(dir, sortBy);

        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new IllegalArgumentException("Il verbale fornito non esiste"));

        Course course = report.getExam().getCourse();
        Professor courseProfessor = course.getProfessor();
        if (!professorId.equals(courseProfessor.getId())) {
            throw new AccessDeniedException("Assicurati di essere il docente associato al corso.");
        }

        List<Registration> registrations = registrationRepository.findByReport_Id(reportId, sort);

        return new ReportDTO(report, registrations);
    }

    @Transactional(readOnly = true)
    public List<ReportDTO> findReportsByProfessorId(Integer professorId) {
        return reportRepository.findAllByProfessor_IdSorted(professorId).stream()
            .map(r -> new ReportDTO(r, List.of()))
            .toList();
    }
}
