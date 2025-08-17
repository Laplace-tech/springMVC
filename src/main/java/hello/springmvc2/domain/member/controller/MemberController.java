package hello.springmvc2.domain.member.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import hello.springmvc2.domain.member.controller.form.MemberSaveForm;
import hello.springmvc2.domain.member.controller.form.MemberUpdateForm;
import hello.springmvc2.domain.member.dto.MemberDto;
import hello.springmvc2.domain.member.service.MemberService;
import hello.springmvc2.domain.member.validator.MemberValidator;
import hello.springmvc2.web.login.service.LoginService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/members")
public class MemberController {

	private final LoginService loginService;
	private final MemberService memberService;
	private final MemberValidator memberValidator;

	@InitBinder("memberSaveForm")
	public void initMemberSaveBinder(WebDataBinder binder) {
		binder.addValidators(memberValidator);
	}
	
    @InitBinder("memberUpdateForm")
    public void initUpdateBinder(WebDataBinder binder) {
        binder.addValidators(memberValidator);
    }
    
	@GetMapping
	public String list(Model model) {
		model.addAttribute("members", memberService.getAllMembers());
		return "members/list";
	}

	@GetMapping("/{memberId}")
	public String detail(@PathVariable("memberId") Long memberId, Model model) {
		model.addAttribute("member", memberService.getMemberDto(memberId));
		return "members/detail";
	}

	@GetMapping("/register")
	public String registerForm(Model model) {
		model.addAttribute("memberSaveForm", new MemberSaveForm());
		return "members/registerForm";
	}

	@PostMapping("/register")
	public String registerForm(@Validated @ModelAttribute MemberSaveForm form, 
						  	   BindingResult bindingResult,
						  	   RedirectAttributes redirectAttributes,
						  	   HttpServletRequest request) {
		if (bindingResult.hasErrors()) {
			log.warn("회원가입 유효성 실패: {}", bindingResult);
			return "members/registerForm";
		}

		MemberDto savedMember = memberService.registerMember(form);
		loginService.createSession(savedMember.getLoginId(), request);
		return "redirect:/";
	}

	@GetMapping("/{memberId}/edit")
	public String editForm(@PathVariable("memberId") Long memberId, Model model) {
		model.addAttribute("memberUpdateForm", memberService.getMemberUpdateForm(memberId));
		return "members/editForm";
	}

	@PostMapping("/{memberId}/edit")
	public String update(@PathVariable("memberId") Long memberId,
						 @Validated @ModelAttribute MemberUpdateForm form, 
						 BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			log.warn("회원 수정 유효성 실패: {}", bindingResult);
			return "members/editForm";
		}

		memberService.updateMember(memberId, form);
		return "redirect:/members";
	}

	@PostMapping("/{memberId}/delete")
	public String delete(@PathVariable("memberId") Long memberId) {
		memberService.deleteMember(memberId);
		return "redirect:/members";
	}

}
