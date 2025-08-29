package hello.springmvc2.web.config;

import java.util.List;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import hello.springmvc2.domain.item.converter.ItemIdToItemConverter;
import hello.springmvc2.domain.item.converter.ItemToStringConverter;
import hello.springmvc2.domain.item.converter.NumberFormatter;
import hello.springmvc2.web.filter.HttpRequestLoggingFilter;
import hello.springmvc2.web.interceptor.LogInterceptor;
import hello.springmvc2.web.interceptor.LoginCheckInterceptor;
import hello.springmvc2.web.login.argumentresolver.LoginMemberArgumentResolver;
import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

	private final ItemToStringConverter itemToStringConverter;
	private final ItemIdToItemConverter itemIdToItemConverter;
	private final NumberFormatter numberFormatter;
	
	private final LoginMemberArgumentResolver loginMemberArgumentResolver;
	
	private final LogInterceptor logInterceptor;
	private final LoginCheckInterceptor loginCheckInterceptor;
	
	private final static String[] STATIC_RESOURCES = { "/css/**", "/*.ico" };
	private final static String[] AUTH_WHITELIST = { "/", "/members/register", 
													 "/login", "/logout", "/error",
													 "/servlet/**", "/spring/*"};
	@Bean
	BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(loginMemberArgumentResolver);
    }
    
//    @Bean
//    FilterRegistrationBean<HttpRequestLoggingFilter> loggingFilter() {
//    	FilterRegistrationBean<HttpRequestLoggingFilter> registrationBean = new FilterRegistrationBean<>();
//    	registrationBean.setFilter(new HttpRequestLoggingFilter());
//    	registrationBean.addUrlPatterns("/*");
//    	registrationBean.setOrder(1);
//    	return registrationBean;
//    }

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
    
	@Override
	public void addFormatters(FormatterRegistry registry) {
		registry.addConverter(itemIdToItemConverter);
		registry.addConverter(itemToStringConverter);
		registry.addFormatter(numberFormatter);
	}
	
	private String[] concatArrays(String[] a, String[] b) {
		String[] result = new String[a.length + b.length];
		System.arraycopy(a, 0, result, 0, a.length);
		System.arraycopy(b, 0, result, a.length, b.length);
		return result;
	}
    
}
