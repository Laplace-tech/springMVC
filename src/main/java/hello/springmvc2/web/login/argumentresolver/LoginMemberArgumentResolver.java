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

@Slf4j
@Component
public class LoginMemberArgumentResolver implements HandlerMethodArgumentResolver {

	@Override
	public boolean supportsParameter(MethodParameter parameter) {
		boolean hasLoginAnnotation = parameter.hasParameterAnnotation(Login.class);
		boolean hasMemberType = LoginMember.class.isAssignableFrom(parameter.getParameterType());
		log.info("supportsParameter - parameterName={}, hasLoginAnnotation={}, hasMemberType={}",
				parameter.getParameterName(), hasLoginAnnotation, hasMemberType);
		return hasLoginAnnotation && hasMemberType;
	}

	@Override
	public Object resolveArgument(MethodParameter parameter,
								  ModelAndViewContainer mavContainer,
								  NativeWebRequest webRequest, 
								  WebDataBinderFactory binderFactory) throws Exception {
		
		HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
        if(request == null) {
        	log.error("HttpServletRequest를 가져올 수 없음");
        	return null;
        }
		
		HttpSession session = request.getSession(false);
		if (session == null) {
            log.info("세션이 존재하지 않음");
            return null;
        }

		LoginMember loginMember = (LoginMember) session.getAttribute(SessionConst.LOGIN_MEMBER);
		if(loginMember == null) {
			log.info("세션에 로그인 멤버 정보가 없음");
			return null;
		}
		
        log.info("세션에서 조회한 로그인 사용자: {}", loginMember);
        return loginMember;
    }
	
}
