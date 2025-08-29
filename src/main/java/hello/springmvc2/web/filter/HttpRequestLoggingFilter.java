package hello.springmvc2.web.filter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Component;
import org.springframework.web.util.ContentCachingRequestWrapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class HttpRequestLoggingFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper(httpRequest);

        System.out.println();
        log.info("HTTP Method = {}, URI = {}", wrappedRequest.getMethod(), wrappedRequest.getRequestURI());
        wrappedRequest.getHeaderNames().asIterator()
        	.forEachRemaining(name 
        			->log.info("Header: {}={}", name, wrappedRequest.getHeader(name)));

        chain.doFilter(wrappedRequest, response);
        
        byte[] content = wrappedRequest.getContentAsByteArray();
        if (content.length > 0) {
            String body = new String(content, StandardCharsets.UTF_8);
            log.info("HTTP Body = {}", body);
        }
    }
}
