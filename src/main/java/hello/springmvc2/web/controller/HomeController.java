package hello.springmvc2.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import hello.springmvc2.domain.member.dto.LoginMember;
import hello.springmvc2.web.login.argumentresolver.Login;

/**
 * 요청 흐름 (GET /)
 * 
 * 1. 클라이언트 요청 
 * -> 사용자가 브라우저에서 루트 URL 접속
 * 
 * 2. DispatcherServlet 진입
 * -> 스프링 MVC의 프론트 컨트롤러(DispatcherServlet)가 요청을 받음
 * 
 * 3. HandlerMapping 탐색
 * -> @GetMapping("/") 애너테이션이 붙은 HomeController.home() 메서드를 찾음
 * 
 * 4. HandlerAdapter 실행
 * -> 스프링은 HomeController를 실행할 때, 메서드 파라미터를 채워야 함.
 *    즉, (@Login LoginMember loginMember, Model model) 이 부분을 채우는 과정이 필요
 *    
 * ---------------------------------------------------------------------------   
 *    
 * ArgumentResolver 동작
 * 
 * 1. 일반 파라미터(Model, HttpServletReqeust 등)
 * -> Model 객체는 스프링이 자동으로 만들어서 넣어줌
 * 
 * 2. @Login LoginMember loginMember
 * -> 여기는 스프링이 기본적으로 처리할 수 없으므로, 커스텀 ArgumentResolver가 개입함
 * 
 * 3. LoginMemberArgumentResolver 내부 동작
 * -> supportsParameter() : 해당 파라미터가 @Login이 붙어있고, 타입이 LoginMember이면 true 반환
 * -> resolveArgument() : 실제 값을 만들어서 반환
 * 
 */

@Controller
public class HomeController {

	@GetMapping("/")
	public String home(@Login LoginMember loginMember, Model model) {
		
		if(loginMember == null) {
			return "home"; // 로그인 안 한 사용자 → 기본 홈
		}
		
		model.addAttribute("member", loginMember);
		return "loginHome"; // 로그인 한 사용자 → 로그인 홈
		
	}
	
}
