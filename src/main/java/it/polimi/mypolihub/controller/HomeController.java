package it.polimi.mypolihub.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import it.polimi.mypolihub.DTO.CourseDTO;
import it.polimi.mypolihub.entity.Role;
import it.polimi.mypolihub.security.CustomUserDetails;
import it.polimi.mypolihub.service.CourseService;

@Controller
public class HomeController {

    @Autowired
    private CourseService courseService;

    @GetMapping({ "/", "/home" })
    public String home(@AuthenticationPrincipal CustomUserDetails principal, Authentication auth, Model model) {
        List<CourseDTO> courses;
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

        model.addAttribute("courses", courses);
        model.addAttribute("helloName", principal.getName());
        model.addAttribute("role", role);

        return "home";
    }
}
