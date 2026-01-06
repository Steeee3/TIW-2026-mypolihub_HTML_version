package it.polimi.mypolihub.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import ch.qos.logback.core.model.Model;
import it.polimi.mypolihub.entity.Role;

@Controller
public class HomeController {
    
    @GetMapping({"/", "/home"})
    public String home(Authentication auth, Model model) {
        Role role = Role.from(auth);

        switch (role) {
            case STUDENT:
                break;
            case PROFESSOR:
                break;
            case ADMIN:
                break;
        }

        return "home";
    }
}
