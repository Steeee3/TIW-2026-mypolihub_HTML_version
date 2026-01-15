package it.polimi.mypolihub.controller;

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

@Controller
public class EsitoController {

	@Autowired
	private ExamService examService;
    
    @GetMapping("/student/result")
    public String result(
			@RequestParam(name = "examId", required = false) Integer examId,
            @AuthenticationPrincipal CustomUserDetails principal,
            Authentication auth,
            Model model) {
		Role role = Role.from(auth);

		if (examId == null) {
			return "redirect:/home";
		}

		try {
			RegistrationDTO registration = examService.getResultByStudentIdAndExamId(principal.getId(), examId);

			model.addAttribute("registration", registration);
			model.addAttribute("notPublished", false);

			if (registration.canBeDeclined()) {
				model.addAttribute("canDecline", true);
			} else {
				model.addAttribute("canDecline", false);
			}
		} catch (IllegalArgumentException e) {
			model.addAttribute("errorMessage", e.getMessage());
			model.addAttribute("notPublished", true);
		}

		model.addAttribute("examId", examId);
		model.addAttribute("helloName", principal.getName());
        model.addAttribute("role", role);

        return "result";
    }

	@PostMapping("/student/result/{examId}/decline")
	public String declineResult(
		@PathVariable Integer examId,
		@AuthenticationPrincipal CustomUserDetails principal,
        RedirectAttributes ra) {
		
		try {
			examService.declineExamResult(principal.getId(), examId);
			ra.addFlashAttribute("successMessage", "Voto rifiutato con successo");
		} catch (IllegalArgumentException e) {
			ra.addFlashAttribute("errorMessage", e.getMessage());
		}

		ra.addAttribute("examId", examId);

		return "redirect:/student/result";
	}
}
