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
        return (session != null && session.getAttribute(SessionConst.LOGIN_MEMBER) != null);
    }

    private void redirectToLogin(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String redirectURI = URLEncoder.encode(request.getRequestURI(), StandardCharsets.UTF_8);
        response.sendRedirect("/login?redirectURL=" + redirectURI);
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
