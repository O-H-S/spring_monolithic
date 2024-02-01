package com.ohs.monolithic.configuration;

import com.ohs.monolithic.utils.ExecutionTimeInterceptor;
import com.ohs.monolithic.utils.StringToBoardPaginationTypeConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.filter.HiddenHttpMethodFilter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

    final private ExecutionTimeInterceptor executionTimeInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(executionTimeInterceptor);
    }

    // 서블릿 필터, 들어온 POST 요청을 "_method" 값으로 메소드를 변경함.
    // 당연히 브라우저에서는 실제 POST로 전송하지만, 스프링에서는 _method로 요청한 것처럼 보임.
    // 스프링 시큐리티 필터보다 앞에 배치되는 것이 바람직해보임. (시큐리티 필터들 중에서는 메소드를 이용한 로직이 존재함)
    @Bean
    public HiddenHttpMethodFilter hiddenHttpMethodFilter() {
        return new HiddenHttpMethodFilter();
    }


    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(new StringToBoardPaginationTypeConverter());
    }
}