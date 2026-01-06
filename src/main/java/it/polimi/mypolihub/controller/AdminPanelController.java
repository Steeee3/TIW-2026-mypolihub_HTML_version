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
import it.polimi.mypolihub.service.UserCreatorService;

@Controller
@RequestMapping("/admin")
public class AdminPanelController {
    
    @Autowired
    private UserCreatorService userCreatorService;

    @GetMapping("/panel")
    public String panel(Model model) {
        model.addAttribute("report", null);
        return "admin/panel";
    }

    @PostMapping("/import-users")
    public String importUsers(@RequestParam("file") MultipartFile file,
                              @RequestParam("role") Role role,
                              @RequestParam("defaultPassword") String defaultPassword,
                              Model model) {
        UserImportReportDTO report = userCreatorService.importUsersFromUpload(file, role, defaultPassword);

        model.addAttribute("report", report);
        return "admin/panel";
    }
}
