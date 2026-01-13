package it.polimi.mypolihub.controller;

import java.util.ArrayList;
import java.util.LinkedHashMap;
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
import it.polimi.mypolihub.service.ReportService;

@Controller
public class VerbaliController {

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
            @RequestParam(name = "reportId", required = false) Integer reportId,
            @RequestParam(name = "sort", required = false) String sort,
            @RequestParam(name = "sortDir", required = false) String sortDir,
            @AuthenticationPrincipal CustomUserDetails principal,
            Authentication auth,
            Model model) {

        if (reportId == null) {
            return showAllReports(principal, auth, model);
        } else {
            return showSingleReport(reportId, sort, sortDir, principal, auth, model);
        }
    }

    private String showSingleReport (
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

        ReportDTO report = reportService.getReportByIdSortedBy(principal.getId(), reportId, sortKey, sortDir);

        model.addAttribute("reportId", reportId);
        model.addAttribute("report", report);

        model.addAttribute("examId", report.getExam().getId());
        model.addAttribute("registrations", report.getRegistrations());

        model.addAttribute("verbalizedCount", report.getRegistrations().size());

        model.addAttribute("sortKey", sort);
        model.addAttribute("sortDir", sortDir);

        model.addAttribute("helloName", principal.getName());
        model.addAttribute("role", role);

        return "report";
    }

    private String showAllReports(
        @AuthenticationPrincipal CustomUserDetails principal,
            Authentication auth,
            Model model) {
            
        Role role = Role.from(auth);

        List<ReportDTO> reports = reportService.findReportsByProfessorId(principal.getId());

        model.addAttribute("reports", reports);
        model.addAttribute("reportsCount", reports.size());

        LinkedHashMap<Integer, List<ReportDTO>> reportsByCourseId = new LinkedHashMap<>();
        LinkedHashMap<Integer, CourseDTO> coursesById = new LinkedHashMap<>();

        for (ReportDTO r : reports) {
            CourseDTO c = r.getExam().getCourse();
            Integer courseId = c.getId();

            coursesById.putIfAbsent(courseId, c);
            reportsByCourseId.computeIfAbsent(courseId, k -> new ArrayList<>()).add(r);
        }

        model.addAttribute("courses", coursesById.values());
        model.addAttribute("reportsByCourseId", reportsByCourseId);
        model.addAttribute("coursesWithReportsCount", coursesById.size());

        model.addAttribute("helloName", principal.getName());
        model.addAttribute("role", role);
        
        return "reports";
    }
}
