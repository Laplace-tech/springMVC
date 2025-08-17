package hello.springmvc2.web.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import hello.springmvc2.web.interceptor.LogInterceptor;
import hello.springmvc2.web.interceptor.LoginCheckInterceptor;
import hello.springmvc2.web.login.argumentresolver.LoginMemberArgumentResolver;
import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

	private final LoginMemberArgumentResolver loginMemberArgumentResolver;
	
	private final LogInterceptor logInterceptor;
	private final LoginCheckInterceptor loginCheckInterceptor;
	
	private final static String[] STATIC_RESOURCES = { "/css/**", "/*.ico" };
	private final static String[] AUTH_WHITELIST = { "/", "/members/register", "/login", "/logout", "/error" };
	
	@Bean
	BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(loginMemberArgumentResolver);
    }
    
    @Override
	public void addInterceptors(InterceptorRegistry registry) {	
		registry.addInterceptor(logInterceptor)
				.order(1)
				.addPathPatterns("/**")
				.excludePathPatterns(STATIC_RESOURCES);
		
		registry.addInterceptor(loginCheckInterceptor)
				.order(2)
				.addPathPatterns("/**")
				.excludePathPatterns(concatArrays(STATIC_RESOURCES, AUTH_WHITELIST));
	}
    
	private String[] concatArrays(String[] a, String[] b) {
		String[] result = new String[a.length + b.length];
		System.arraycopy(a, 0, result, 0, a.length);
		System.arraycopy(b, 0, result, a.length, b.length);
		return result;
	}
	
    
}
