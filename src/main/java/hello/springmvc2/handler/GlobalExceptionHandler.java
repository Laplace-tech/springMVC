package hello.springmvc2.handler;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import hello.springmvc2.domain.item.exception.ItemNotFoundException;
import hello.springmvc2.domain.member.exception.MemberNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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
 * "error/errorPage" 반환 -> 사용자에게 HTML 전달
 */

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

	private static final DateTimeFormatter TIMESTAMP_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
	private static final Map<Class<? extends Throwable>, HttpStatus> EXCEPTION_STATUS_MAP = 
			Map.ofEntries(
					Map.entry(ItemNotFoundException.class, HttpStatus.NOT_FOUND),
					Map.entry(MemberNotFoundException.class, HttpStatus.NOT_FOUND),
					Map.entry(NoResourceFoundException.class, HttpStatus.NOT_FOUND),
					Map.entry(NoHandlerFoundException.class, HttpStatus.NOT_FOUND),
					Map.entry(IllegalArgumentException.class, HttpStatus.BAD_REQUEST)
					);
	
	@ExceptionHandler(Exception.class)
	public String handlerExceptions(Exception ex, HttpServletRequest request, HttpServletResponse response, Model model) {
		
		String uri = request.getRequestURI();
		
		// ✅ 파일 다운로드 요청이면 뷰 렌더링 X
		if (uri.startsWith("/files")) {
			HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
			log.warn("[File Error : {}], URI = {}, message = {}", status.value(), uri, ex.getMessage());
			return null;
		}
		
		// 일반 웹 요청 처리
		HttpStatus status = EXCEPTION_STATUS_MAP.getOrDefault(ex.getClass(), HttpStatus.INTERNAL_SERVER_ERROR);
		response.setStatus(status.value());
		log.warn("[Error : {}], URI = {}, message = {}", status.value(), request.getRequestURI(), ex.getMessage());
		
		Map<String, Object> errorAttributes = setErrorAttributes(request, status, ex);
		model.addAllAttributes(errorAttributes);
		return "error/errorPage";
	}

	private Map<String, Object> setErrorAttributes(HttpServletRequest request, HttpStatus status, Exception ex) {
		Map<String, Object> errorAttributes = new LinkedHashMap<>();
		errorAttributes.put("status", status.value());
		errorAttributes.put("error", status.getReasonPhrase());
		errorAttributes.put("timestamp", LocalDateTime.now().format(TIMESTAMP_FORMATTER));
		errorAttributes.put("path", request.getRequestURI());
		errorAttributes.put("message", ex.getMessage());
		errorAttributes.put("exception", ex.getClass().getSimpleName());
		errorAttributes.put("trace", getStackTraceAsString(ex));
		
		return errorAttributes;
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
