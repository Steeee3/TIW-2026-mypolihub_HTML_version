package it.polimi.mypolihub.controller;

import java.time.LocalDateTime;

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
import it.polimi.mypolihub.repository.CourseRepository;
import it.polimi.mypolihub.repository.MajorRepository;
import it.polimi.mypolihub.repository.ProfessorRepository;
import it.polimi.mypolihub.repository.UserRepository;
import it.polimi.mypolihub.service.CourseService;
import it.polimi.mypolihub.service.ExamService;
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
    private CourseService courseService;

    @Autowired
    private ExamService examService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProfessorRepository professorRepository;

    @Autowired
    private MajorRepository majorRepository;

    @Autowired
    private CourseRepository courseRepository;

    @GetMapping("/panel")
    public String panel(Model model) {
        model.addAttribute("majors", majorRepository.findAll());
        model.addAttribute("usersCount", userRepository.count());
        model.addAttribute("coursesCount", courseRepository.count());
        model.addAttribute("professors", professorRepository.findAllWithUser());
        model.addAttribute("courses", courseRepository.findAll());
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
        model.addAttribute("professors", professorRepository.findAllWithUser());
        model.addAttribute("coursesCount", courseRepository.count());
        model.addAttribute("courses", courseRepository.findAll());
        model.addAttribute("report", report);

        return "admin/panel";
    }

    @PostMapping("/users")
    public String importSingleUser(
            @RequestParam("role") Role role,
            @RequestParam("name") String name,
            @RequestParam("surname") String surname,
            @RequestParam("password") String password,
            @RequestParam(value = "majorId", required = false) Integer majorId,
            Model model) {
        UserImportReportDTO report = userCreatorService.createSingleUser(role, name, surname, password, majorId);

        model.addAttribute("majors", majorRepository.findAll());
        model.addAttribute("usersCount", userRepository.count());
        model.addAttribute("professors", professorRepository.findAllWithUser());
        model.addAttribute("coursesCount", courseRepository.count());
        model.addAttribute("courses", courseRepository.findAll());
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
        model.addAttribute("majors", majorRepository.findAll());
        model.addAttribute("usersCount", userRepository.count());
        model.addAttribute("professors", professorRepository.findAllWithUser());
        model.addAttribute("coursesCount", courseRepository.count());
        model.addAttribute("courses", courseRepository.findAll());

        return "admin/panel";
    }

    @PostMapping("/courses")
    public String createCourse(
            @RequestParam("courseName") String courseName,
            @RequestParam("cfu") Integer cfu,
            @RequestParam("majorId") Integer majorId,
            @RequestParam("professorId") Integer professorId,
            Model model) {
        try {
            courseService.createCourse(courseName, cfu, majorId, professorId);
            model.addAttribute("majorMsg", "Corso creato: " + courseName);
        } catch (IllegalArgumentException e) {
            model.addAttribute("majorError", e.getMessage());
        }

        model.addAttribute("report", null);
        model.addAttribute("majors", majorRepository.findAll());
        model.addAttribute("usersCount", userRepository.count());
        model.addAttribute("professors", professorRepository.findAllWithUser());
        model.addAttribute("coursesCount", courseRepository.count());
        model.addAttribute("courses", courseRepository.findAll());

        return "admin/panel";
    }

    @PostMapping("/exams")
    public String createExamCall(@RequestParam("courseId") Integer courseId, @RequestParam("date") LocalDateTime date,
            Model model) {
        try {
            examService.addExamCall(courseId, date);
            model.addAttribute("examMsg", "Esame aggiunto");
        } catch (IllegalArgumentException e) {
            model.addAttribute("examError", e.getMessage());
        }

        model.addAttribute("report", null);
        model.addAttribute("majors", majorRepository.findAll());
        model.addAttribute("usersCount", userRepository.count());
        model.addAttribute("professors", professorRepository.findAllWithUser());
        model.addAttribute("coursesCount", courseRepository.count());
        model.addAttribute("courses", courseRepository.findAll());

        return "admin/panel";
    }
}
