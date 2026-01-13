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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import it.polimi.mypolihub.DTO.RegistrationDTO;
import it.polimi.mypolihub.entity.Role;
import it.polimi.mypolihub.security.CustomUserDetails;
import it.polimi.mypolihub.service.ExamService;
import it.polimi.mypolihub.service.ResultService;

@Controller
public class IscrittiController {

    @Autowired
    private ExamService examService;

    @Autowired
    private ResultService resultService;

    private static final String DEFAULT_SORT = "student.number";
    private static final String DEFAULT_DIR = "asc";

    private static final Set<String> ALLOWED_SORTS = Set.of(
            "student.number",
            "student.surname",
            "student.name",
            "student.email",
            "result",
            "status");
    private final Map<String, String> SORT_MAPPING = Map.of(
            "student.surname", "student.user.surname",
            "student.name", "student.user.name",
            "student.email", "student.user.email",
            "result", "result.id",
            "status", "status.id");

    @GetMapping("/professor/exam")
    public String iscritti(
            @RequestParam(name = "examId", required = false) Integer examId,
            @RequestParam(name = "sort", required = false) String sort,
            @RequestParam(name = "sortDir", required = false) String sortDir,
            @RequestParam(name = "editStudentNumber", required = false) Integer editStudentNumber,
            @AuthenticationPrincipal CustomUserDetails principal,
            Authentication auth,
            Model model) {
        Role role = Role.from(auth);

        if (examId == null) {
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

        List<RegistrationDTO> registrations = examService.getStudentsByExamIdSortedBy(principal.getId(), examId, sortKey, sortDir);

        model.addAttribute("examId", examId);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("sortKey", sort);

        model.addAttribute("registrations", registrations);
        model.addAttribute("results", resultService.getAllResults());
        model.addAttribute("editStudentNumber", editStudentNumber);

        model.addAttribute("helloName", principal.getName());
        model.addAttribute("role", role);

        return "iscritti";
    }

    @PostMapping("/professor/registrations/{registrationId}/result")
    public String editResult(
        @PathVariable Integer registrationId,
        @RequestParam Integer resultId,
        @RequestParam(name = "examId", required = false) Integer examId,
        @RequestParam(name = "sort", required = false) String sort,
        @RequestParam(name = "sortDir", required = false) String sortDir,
        @AuthenticationPrincipal CustomUserDetails principal,
        RedirectAttributes ra
    ) {
        examService.setResult(principal.getId(), registrationId, resultId);
        if (examId == null) {
            return "redirect:/home";
        }

        ra.addAttribute("examId", examId);
        ra.addAttribute("sort", sort);
        ra.addAttribute("sortDir", sortDir);

        return "redirect:/professor/exam";
    }

    @PostMapping("/professor/exam/{examId}/publish")
    public String publishResults(
        @PathVariable Integer examId,
        @RequestParam(name = "sort", required = false) String sort,
        @RequestParam(name = "sortDir", required = false) String sortDir,
        @AuthenticationPrincipal CustomUserDetails principal,
        RedirectAttributes ra
    ) {
        examService.publishResults(principal.getId(), examId);

        ra.addAttribute("examId", examId);
        ra.addAttribute("sort", sort);
        ra.addAttribute("sortDir", sortDir);

        return "redirect:/professor/exam";
    }

    @PostMapping("/professor/exam/{examId}/finalize")
    public String finalizeResults(
        @PathVariable Integer examId,
        @RequestParam(name = "sort", required = false) String sort,
        @RequestParam(name = "sortDir", required = false) String sortDir,
        @AuthenticationPrincipal CustomUserDetails principal,
        RedirectAttributes ra
    ) {
        examService.finalizeResults(principal.getId(), examId);

        ra.addAttribute("examId", examId);
        ra.addAttribute("sort", sort);
        ra.addAttribute("sortDir", sortDir);

        return "redirect:/professor/exam";
    }
}
