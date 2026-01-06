package it.polimi.mypolihub.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import it.polimi.mypolihub.entity.Role;
import it.polimi.mypolihub.entity.User;
import it.polimi.mypolihub.repository.UserRepository;

@Controller
public class TestController {

    @Autowired
    private UserRepository userRepository;
    
    @GetMapping("/prova")
    public String prova(Model model) {
        model.addAttribute("msg", "Ciao da Spring!");
        return "prova";
    }

    @GetMapping("/dbtest")
    public String dbtest(Model model) {
        User u = new User();
        u.setName("Test");
        u.setSurname("User");
        u.setEmail("test" + System.currentTimeMillis() + "@example.com");
        u.setPassword("x");
        u.setRole(Role.STUDENT);

        userRepository.save(u);

        model.addAttribute("count", userRepository.count());
        model.addAttribute("lastEmail", u.getEmail());
        return "dbtest";
    }
}
