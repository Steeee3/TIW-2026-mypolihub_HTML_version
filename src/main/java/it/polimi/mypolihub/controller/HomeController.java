package it.polimi.mypolihub.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
                exams = examService.getExamsForCourse(courseId);
                break;
            case PROFESSOR:
                courses = courseService.findCoursesByProfessorId(principal.getId());
                break;
            case ADMIN:
                return "redirect:/admin/panel";
            default:
                courses = List.of();
        }

        if (courseId != null && courses.stream().anyMatch(c -> c.getId() == courseId)) {
            selectedCourseId = courseId;
            exams = examService.getExamsForCourse(selectedCourseId);
        }

        model.addAttribute("courses", courses);
        model.addAttribute("selectedCourseId", selectedCourseId);
        model.addAttribute("exams", exams);
        
        model.addAttribute("helloName", principal.getName());
        model.addAttribute("role", role);

        return "home";
    }
}
