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

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler({ItemNotFoundException.class, MemberNotFoundException.class})
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public String handleItemNotFound(RuntimeException ex, HttpServletRequest request, Model model) {
		log.warn("[404 오류] uri={}, message={}", request.getRequestURI(), ex.getMessage());
		setCommonModel(model, HttpStatus.NOT_FOUND, ex, request);
		return "error/errorPage";
	}

	@ExceptionHandler(NoResourceFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public String handleNotFound(NoResourceFoundException ex, HttpServletRequest request, Model model) {
		log.warn("[404 오류] uri={}, message={}", request.getRequestURI(), ex.getMessage());
		setCommonModel(model, HttpStatus.NOT_FOUND, ex, request);
		return "error/errorPage";
	}

	@ExceptionHandler(IllegalArgumentException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public String handleBadRequest(IllegalArgumentException ex, HttpServletRequest request, Model model) {
		log.warn("[400 오류] uri={}, message={}", request.getRequestURI(), ex.getMessage());
		setCommonModel(model, HttpStatus.BAD_REQUEST, ex, request);
		return "error/errorPage";
	}

	@ExceptionHandler(Exception.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public String handleServerError(Exception ex, Model model, HttpServletRequest request) {
		log.warn("[500 오류] uri={}, message={}", request.getRequestURI(), ex.getMessage());
		setCommonModel(model, HttpStatus.INTERNAL_SERVER_ERROR, ex, request);
		return "error/errorPage";
	}
	
	private void setCommonModel(Model model, HttpStatus status, Exception ex, HttpServletRequest request) {
		model.addAllAttributes(Map.of(
				"status", status.value(), 
				"error", status.getReasonPhrase(),
				"message", ex.getMessage(),
				"path", request.getRequestURI(), 
				"timestamp", LocalDateTime.now(), 
				"exception", ex.getClass().getSimpleName(),
				"trace", getStackTraceAsString(ex)
		));
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
