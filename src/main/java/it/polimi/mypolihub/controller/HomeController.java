package it.polimi.mypolihub.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import ch.qos.logback.core.model.Model;
import it.polimi.mypolihub.entity.Role;
import it.polimi.mypolihub.service.UserCreatorService;

@Controller
public class HomeController {

    @Autowired
    private UserCreatorService userCreatorService;
    
    @GetMapping({"/", "/home"})
    public String home(Authentication auth, Model model) {
        userCreatorService.importUsersFromFile("admin.txt", Role.ADMIN, "password");
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
