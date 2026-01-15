package it.polimi.mypolihub.controller;

import java.util.List;

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

import it.polimi.mypolihub.DTO.CourseDTO;
import it.polimi.mypolihub.DTO.ExamDTO;
import it.polimi.mypolihub.entity.Role;
import it.polimi.mypolihub.security.CustomUserDetails;
import it.polimi.mypolihub.service.CourseService;
import it.polimi.mypolihub.service.ExamService;

@Controller
public class HomeController {

    @Autowired
    private CourseService courseService;

    @Autowired
    private ExamService examService;

    @GetMapping({ "/", "/home" })
    public String home(
            @RequestParam(name = "courseId", required = false) Integer courseId,
            @AuthenticationPrincipal CustomUserDetails principal,
            Authentication auth,
            Model model) {

        List<CourseDTO> courses;
        List<ExamDTO> exams = List.of();
        Integer selectedCourseId = null;

        Role role = Role.from(auth);

        switch (role) {
            case STUDENT:
                courses = courseService.findCoursesByStudentId(principal.getId());
                break;
            case PROFESSOR:
                courses = courseService.findCoursesByProfessorId(principal.getId());
                break;
            case ADMIN:
                return "redirect:/admin/panel";
            default:
                courses = List.of();
        }

        if (courseId != null && courses.stream().anyMatch(c -> c.getId().equals(courseId))) {
            selectedCourseId = courseId;
            exams = examService.getExamsForCourse(selectedCourseId);
        }

        if (selectedCourseId != null && role == Role.STUDENT) {
            model.addAttribute("registeredExamIds", examService.getRegisteredExamIds(principal.getId(), selectedCourseId));
        }

        model.addAttribute("courses", courses);
        model.addAttribute("selectedCourseId", selectedCourseId);
        model.addAttribute("exams", exams);
        
        model.addAttribute("helloName", principal.getName());
        model.addAttribute("role", role);

        return "home";
    }

    @PostMapping("/student/exam/{examId}/register")
    public String registerForExam(
        @PathVariable Integer examId,
        @RequestParam(name = "courseId", required = false) Integer courseId,
        @AuthenticationPrincipal CustomUserDetails principal,
        RedirectAttributes ra
    ) {
        examService.registerStudentForExam(principal.getId(), examId);

        if (courseId != null) {
            ra.addAttribute("courseId", courseId);
            return "redirect:/home#course-" + courseId;
        }

        return "redirect:/home";
    }
}
