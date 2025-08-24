package hello.springmvc2.handler;

import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import hello.springmvc2.domain.item.exception.ItemNotFoundException;
import hello.springmvc2.domain.member.exception.MemberNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
/**
 * [스프링 MVC 예외 처리 계층 구조]
 * 
 * 스프링 MVC는 요청 -> 컨트롤러 -> 응답 흐름에서 예외가 생기면, 자동으로 처리해 주는 구조가 있다
 * 
 * 핵심 컴포넌트
 * 1. DispatcherServlet
 *  -> 스프링 MVC의 진입점
 *  -> 모든 요청을 받고, 컨트롤러를 호출.
 *  -> 컨트롤러에서 예외가 발생하면 HandlerExceptionResolver를 호출
 *  
 * 2. HandlerExceptionResolver
 *  -> 컨트롤러에서 발생한 예외를 잡아서 처리
 *  -> 스프링 기본 제공 3가지:
 *  	1. ExceptionHandlerExceptionResolver : @ExceptionHandler 처리
 * 		2. ResponseStatusExceptionResolver : @ResponseStatus 나 @ResponseStatusException 처리
 * 		3. DefaultHandlerExceptionResolver : 스프링 내부 예외(타입 변환 실패) 처리
 *
 * 3. ControllerAdvice : @ControllerAdvice
 *  -> 전역 예외 처리
 *  -> 특정 컨트롤러가 아니라 애플리케이션 전체에서 발생한 예외 처리 가능
 *  -> @ExceptionHandler 와 결합하면 특정 예외를 글로벌하게 처리
 * 
 * --------------------------------------------------------------------------------
 * 
 * @ContollerAdvice
 * -> 전역 컨트롤러 예외 처리기
 * -> Spring MVC에서 모든 컨트롤러의 예외를 한 곳에서 처리할 수 있음
 * -> 개별 컨트롤러에 @ExceptionHandler 를 두지 않아도 됨
 * 
 * @ExceptionHandler
 * -> 특정 예외가 발생했을 때 처리할 메서드 지정 (예 : ItemNotFoundException -> handleItemNotFound())
 * 
 * @ResponseStatus
 * -> 반환될 HTTP 상태 코드 지정 (HttpStatus.NOT_FOUND -> 404 응답)
 * 
 * --------------------------------------------------------------------------------
 * 
 * [사용자 요청] -> DispatcherServlet -> Controller 실행 -> 예외 발생
 *    │
 *    ▼ 
 * [GlobalExceptionHandler] -> @ExceptionHandler 매핑 -> handleItemNotFound()..
 *    │
 *    ▼
 * setCommonModel() -> model 에 에러 정보 저장
 *    │
 *    ▼
 * "error/errorPage" 반환 -> thymeleaf 렌더링 -> 사용자에게 HTML 전달
 */

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    // ---------------------- 404 NOT FOUND ----------------------
    @ExceptionHandler({ItemNotFoundException.class, MemberNotFoundException.class, NoResourceFoundException.class, NoHandlerFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleNotFound(RuntimeException ex, HttpServletRequest request, Model model) {
        return handleException(HttpStatus.NOT_FOUND, ex, request, model, "[404 오류]");
    }

    // ---------------------- 400 BAD REQUEST ----------------------
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleBadRequest(IllegalArgumentException ex, HttpServletRequest request, Model model) {
        return handleException(HttpStatus.BAD_REQUEST, ex, request, model, "[400 오류]");
    }

    // ---------------------- 500 INTERNAL SERVER ERROR ----------------------
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleServerError(Exception ex, HttpServletRequest request, Model model) {
        return handleException(HttpStatus.INTERNAL_SERVER_ERROR, ex, request, model, "[500 오류]");
    }
	
	private String handleException(HttpStatus status, Exception ex, HttpServletRequest request, Model model, String logPrefix) {
		log.warn("{} uri={}, message={}", logPrefix, request.getRequestURI(), ex.getMessage());
		model.addAllAttributes(Map.of( 
				"status", status.value(),
				"error", status.getReasonPhrase(),
				"timestamp", LocalDateTime.now(),
				"path", request.getRequestURI(),
				"message", ex.getMessage(),
				"exception", ex.getClass().getSimpleName(),
				"trace", getStackTraceAsString(ex)
				));
		return "error/errorPage";
	}
	
    // --- 유틸: stack trace 문자열로 변환 ---
    private String getStackTraceAsString(Exception ex) {
        StringBuilder sb = new StringBuilder();
        for (StackTraceElement element : ex.getStackTrace()) {
            sb.append(element.toString()).append("\n");
        }
        return sb.toString();
    }
	
}
