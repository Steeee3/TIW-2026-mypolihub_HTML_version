package it.polimi.mypolihub.controller;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import it.polimi.mypolihub.DTO.CourseDTO;
import it.polimi.mypolihub.DTO.ReportDTO;
import it.polimi.mypolihub.entity.Role;
import it.polimi.mypolihub.security.CustomUserDetails;
import it.polimi.mypolihub.service.CourseService;
import it.polimi.mypolihub.service.ReportService;

@Controller
public class VerbaliController {

    @Autowired
    private CourseService courseService;

    @Autowired
    private ReportService reportService;

    private static final String DEFAULT_SORT = "student.number";
    private static final String DEFAULT_DIR = "asc";

    private static final Set<String> ALLOWED_SORTS = Set.of(
            "student.number",
            "student.surname",
            "student.name",
            "student.email",
            "result");
    private final Map<String, String> SORT_MAPPING = Map.of(
            "student.surname", "student.user.surname",
            "student.name", "student.user.name",
            "student.email", "student.user.email",
            "result", "result.id");

    @GetMapping("/professor/reports")
    public String verbali(
            @RequestParam(name = "courseId", required = false) Integer courseId,
            @RequestParam(name = "reportId", required = false) Integer reportId,
            @RequestParam(name = "sort", required = false) String sort,
            @RequestParam(name = "sortDir", required = false) String sortDir,
            @AuthenticationPrincipal CustomUserDetails principal,
            Authentication auth,
            Model model) {

        if (reportId == null) {
            return showAllCoursesAndReportsIdRequested(courseId, principal, auth, model);
        } else {
            return showSingleReport(reportId, sort, sortDir, principal, auth, model);
        }
    }

    private String showSingleReport(
            @RequestParam(name = "reportId", required = false) Integer reportId,
            @RequestParam(name = "sort", required = false) String sort,
            @RequestParam(name = "sortDir", required = false) String sortDir,
            @AuthenticationPrincipal CustomUserDetails principal,
            Authentication auth,
            Model model) {

        Role role = Role.from(auth);

        if (reportId == null) {
            return "redirect:/home";
        }

        sort = (sort == null || sort.isBlank()) ? DEFAULT_SORT : sort;
        if (!ALLOWED_SORTS.contains(sort)) {
            sort = DEFAULT_SORT;
        }
        String sortKey = SORT_MAPPING.getOrDefault(sort, sort);

        if (sortDir == null || sortDir.isBlank()) {
            sortDir = DEFAULT_DIR;
        }

        ReportDTO report = null;
        try {
            report = reportService.getReportByIdSortedBy(principal.getId(), reportId, sortKey, sortDir);

            model.addAttribute("report", report);

            model.addAttribute("examId", report.getExam().getId());
            model.addAttribute("registrations", report.getRegistrations());

            model.addAttribute("verbalizedCount", report.getRegistrations().size());
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
        }

        model.addAttribute("reportId", reportId);

        model.addAttribute("sortKey", sort);
        model.addAttribute("sortDir", sortDir);

        model.addAttribute("helloName", principal.getName());
        model.addAttribute("role", role);

        return "report";
    }

    private String showAllCoursesAndReportsIdRequested(
            @RequestParam(name = "courseId", required = false) Integer courseId,
            @AuthenticationPrincipal CustomUserDetails principal,
            Authentication auth,
            Model model) {
        Role role = Role.from(auth);

        List<CourseDTO> courses = courseService.findCoursesByProfessorId(principal.getId()).reversed();

        List<ReportDTO> reports = List.of();
        if (teachesRequestedCourse(courseId, courses)) {
            reports = reportService.getReportsForCourse(principal.getId(), courseId);
        }

        fillExamsAndReportModel(principal, role, courseId, courses, reports, model);

        return "reports";
    }

    private boolean teachesRequestedCourse(Integer courseId, List<CourseDTO> courses) {
        if (courseId == null || courses.isEmpty()) {
            return false;
        }

        return courses.stream()
                .anyMatch(c -> c.getId().equals(courseId));
    }

    private void fillExamsAndReportModel(CustomUserDetails principal, Role role, Integer courseId,
            List<CourseDTO> courses, List<ReportDTO> reports, Model model) {
        model.addAttribute("selectedCourseId", courseId);
        model.addAttribute("courses", courses);
        model.addAttribute("reports", reports);

        model.addAttribute("helloName", principal.getName());
        model.addAttribute("role", role);
    }
}
