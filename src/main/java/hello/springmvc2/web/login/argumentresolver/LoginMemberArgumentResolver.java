package hello.springmvc2.web.login.argumentresolver;

import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import hello.springmvc2.domain.member.dto.LoginMember;
import hello.springmvc2.web.session.SessionConst;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

/**
 * HandlerMethodArgumentResolver의 개념
 * - 스프링 MVC는 컨트롤러의 메서드 파라미터에 값을 넣어줄 때 ArgumentResolver라는 확장 기능을 사용함
 * - 직접 구현하면 **특정 조건**을 만족하는 파라미터에 원하는 값을 주입할 수 있음.
 */
@Slf4j
@Component
public class LoginMemberArgumentResolver implements HandlerMethodArgumentResolver {

	/**
	 * supportsParameter
	 *  -> 스프링이 컨트롤러 파라미터를 볼 때마다 이 메서드를 호출함.
	 * 
	 * 1. parameter.hasParameterAnnotation(Login.class)
	 * 	-> 해당 파라미터에 @Login 애너테이션이 붙어 있는지 확인
	 * 2. LoginMember.class.isAssignableFrom(parameter.getParameterType())
	 *  -> 파라미터 타입이 LoginMember인지 확인
	 *  
	 * 둘 다 만족하면 true 반환 
	 */
	@Override
	public boolean supportsParameter(MethodParameter parameter) {
		boolean hasLoginAnnotation = parameter.hasParameterAnnotation(Login.class);
		boolean hasMemberType = LoginMember.class.isAssignableFrom(parameter.getParameterType());
		
		log.info("supportsParameter - parameterName={}, hasLoginAnnotation={}, hasMemberType={}",
				parameter.getParameterName(), hasLoginAnnotation, hasMemberType);
		return hasLoginAnnotation && hasMemberType;
	}

	/**
	 * resolveArgument 
	 *  -> 실제 파라미터 값을 만들어주는 곳
	 */
	@Override
	public Object resolveArgument(MethodParameter parameter,
								  ModelAndViewContainer mavContainer,
								  NativeWebRequest webRequest, 
								  WebDataBinderFactory binderFactory) throws Exception {
		
		/**
		 * webRequest.getNativeRequest(HttpServletRequest.class) 
		 * -> 현재 요청의 원본(HttpServletRequest) 객체 꺼내오기
		 */
		HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
        if(request == null) {
        	log.error("HttpServletRequest를 가져올 수 없음");
        	return null;
        }
		
		/**
		 * request.getSession(false) 
		 * -> 세션이 있으면 반환, 없으면 null(새로 만들지 않음)
		 */
		HttpSession session = request.getSession(false);
		if (session == null) {
            log.info("세션이 존재하지 않음");
            return null;
        }

		/**
		 * session.getAttribute(SessionConst.LOGIN_MEMBER) 
		 * -> 세션에 저장된 로그인 멤버 객체 꺼냄, 없다면 null 반환
		 */
		LoginMember loginMember = (LoginMember) session.getAttribute(SessionConst.LOGIN_MEMBER);
		if(loginMember == null) {
			log.info("세션에 로그인 멤버 정보가 없음");
			return null;
		}
		
        log.info("세션에서 조회한 로그인 사용자: {}", loginMember);
        return loginMember;
    }
	
}
