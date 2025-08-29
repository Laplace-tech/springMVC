package hello.springmvc2.web.interceptor;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import hello.springmvc2.web.session.SessionConst;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class LoginCheckInterceptor implements HandlerInterceptor {

    /**
     * 컨트롤러 실행 전 호출
     * 로그인 여부 확인 → 없으면 로그인 페이지로 리다이렉트
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String requestURI = request.getRequestURI();
        log.info("[인터셉터 preHandle] 요청 URI: {}", requestURI);

        if (isLoggedIn(request)) {
            log.info("[인증 성공] URI: {}", requestURI);
            return true;
        }

        log.warn("[미인증 사용자 요청 차단] URI: {}", requestURI);
        redirectToLogin(request, response);
        return false;
    }

    /**
     * 컨트롤러 실행 후, 뷰 렌더링 직전 호출
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response,
                           Object handler, ModelAndView modelAndView) {
        log.info("[인터셉터 postHandle] URI: {}, ModelAndView: {}", request.getRequestURI(), modelAndView);
    }

    /**
     * 뷰 렌더링 완료 후 호출 (리소스 정리, 예외 확인 가능)
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                Object handler, Exception ex) {
        log.info("[인터셉터 afterCompletion] URI: {}", request.getRequestURI());
        if (ex != null) {
            log.error("[예외 발생] 요청 처리 중 오류", ex);
        }
    }

    // ============ private 메서드들 ============

    private boolean isLoggedIn(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        return (session != null && 
        		session.getAttribute(SessionConst.LOGIN_MEMBER) != null);
    }

    /**
     * URL vs URI
     * 
     * URI (Uniform Resource Identifier) 
     * - 자원의 식별자
     * - "이 문자열로 자원이 무엇인지 식별할 수 있다"는 의미만 있음
     * - 접근 방법이나 위치까지는 포함하지 않아도 된다.
     * 
     * URL (Uniform Resource Locator) : URI + 접근 방법 포함
     * - URI의 한 종류로, 자원에 접근할 수 있는 방법까지 포함한 문자열 입니다.
     * - 보통 "프로토콜://도메인/경로?쿼리파라미터" 형태
     * 
     */
    private void redirectToLogin(HttpServletRequest request, HttpServletResponse response) throws IOException {
    	// 요청 URL : http://localhost:8080/items/123 
    	String requestURI = request.getRequestURI(); // 결과 : /items/123
    	
    	String encodedURI = URLEncoder.encode(requestURI, StandardCharsets.UTF_8);
    	String redirectURL = "/login?redirectURL=" + encodedURI;
    	
    	response.sendRedirect(redirectURL);
    	log.info("[리다이렉트] redirect : {}", redirectURL);
    }

    // ============ Bean Lifecycle ============

    @PostConstruct
    public void init() {
        log.info("LoginCheckInterceptor Bean 초기화");
    }

    @PreDestroy
    public void destroy() {
        log.info("LoginCheckInterceptor Bean 소멸");
    }
}
