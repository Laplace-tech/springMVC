package hello.springmvc2.web.login.controller;

import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import hello.springmvc2.web.login.form.LoginForm;
import hello.springmvc2.web.login.service.LoginResult;
import hello.springmvc2.web.login.service.LoginService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequiredArgsConstructor
public class LoginController {

	private final LoginService loginService;

	@GetMapping("/login")
	public String loginForm(LoginForm form) {
		return "login/loginForm";
	}

	@PostMapping("/login")
	public String login(@Validated LoginForm form, BindingResult bindingResult, 
						@RequestParam(value = "redirectURL", defaultValue = "/") String redirectURL, 
						HttpServletRequest request) {

	    if (bindingResult.hasErrors()) {
	        return "login/loginForm";
	    }

	    String loginID = form.getLoginId();
	    String rawPassword = form.getPassword();
	    LoginResult loginResult = loginService.loginCheckAuthentication(loginID, rawPassword);

	    if(loginResult == LoginResult.SUCCESS) {
	    	loginService.createSession(form.getLoginId(), request);
	    	return "redirect:" + redirectURL;
	    }
	    
	    handleLoginFailure(loginResult, bindingResult);
	    return "login/loginForm";
	}
	
	@PostMapping("/logout")
	public String logout(HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		if(session != null) {
			session.invalidate();
		}
		return "redirect:/";
	}

	private void handleLoginFailure(LoginResult loginResult, BindingResult bindingResult) {
		switch (loginResult) {
		case USERNAME_NOT_FOUND -> bindingResult.rejectValue("loginId", "login.loginIdNotFound");
		case PASSWORD_MISMATCH -> bindingResult.rejectValue("password", "login.passwordMismatch");
		default -> bindingResult.reject("login.loginFailure");
		}
	}
}
