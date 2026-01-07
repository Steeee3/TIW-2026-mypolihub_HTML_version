package it.polimi.mypolihub.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import it.polimi.mypolihub.DTO.UserImportReportDTO;
import it.polimi.mypolihub.entity.Role;
import it.polimi.mypolihub.repository.MajorRepository;
import it.polimi.mypolihub.repository.UserRepository;
import it.polimi.mypolihub.service.MajorService;
import it.polimi.mypolihub.service.UserCreatorService;

@Controller
@RequestMapping("/admin")
public class AdminPanelController {

    @Autowired
    private UserCreatorService userCreatorService;

    @Autowired
    private MajorService majorService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MajorRepository majorRepository;

    @GetMapping("/panel")
    public String panel(Model model) {
        model.addAttribute("majors", majorRepository.findAll());
        model.addAttribute("usersCount", userRepository.count());
        model.addAttribute("report", null);

        return "admin/panel";
    }

    @PostMapping("/import-users")
    public String importUsers(@RequestParam("file") MultipartFile file,
            @RequestParam("role") Role role,
            @RequestParam("defaultPassword") String defaultPassword,
            @RequestParam(value = "majorId", required = false) Integer majorId,
            Model model) {
        UserImportReportDTO report = userCreatorService.importUsersFromUpload(file, role, defaultPassword, majorId);

        model.addAttribute("majors", majorRepository.findAll());
        model.addAttribute("usersCount", userRepository.count());
        model.addAttribute("report", report);

        return "admin/panel";
    }

    @PostMapping("/majors")
    public String createMajor(@RequestParam("majorName") String majorName,
            Model model) {

        try {
            majorService.createMajor(majorName);
            model.addAttribute("majorMsg", "Major creata: " + majorName);
        } catch (IllegalArgumentException e) {
            model.addAttribute("majorError", e.getMessage());
        }

        model.addAttribute("report", null);
        model.addAttribute("usersCount", userRepository.count());
        model.addAttribute("coursesCount", null);

        return "admin/panel";
    }
}
