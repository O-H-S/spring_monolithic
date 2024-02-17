package com.ohs.monolithic;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.header.writers.frameoptions.XFrameOptionsHeaderWriter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@TestConfiguration
@EnableWebSecurity
public class SecurityConfigForUnitTest {

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
            .authorizeHttpRequests((authorizeHttpRequests) -> authorizeHttpRequests
                    .requestMatchers(new AntPathRequestMatcher("/**")).permitAll())
            //위는 모든 인증되지 않은 요청을 허락한다는 의미이다. 따라서 로그인을 하지 않더라도 모든 페이지에 접근할 수 있다.

            .csrf(AbstractHttpConfigurer::disable)
            // /h2-console/로 시작하는 URL은 CSRF 검증을 하지 않는다는 설정을 추가했다.

            .headers((headers) -> headers
                    .addHeaderWriter(new XFrameOptionsHeaderWriter(
                            XFrameOptionsHeaderWriter.XFrameOptionsMode.SAMEORIGIN)))
            /*
            URL 요청시 X-Frame-Options 헤더값을 sameorigin으로 설정하여 오류가 발생하지 않도록 했다.
            X-Frame-Options 헤더의 값으로 sameorigin을 설정하면 frame에 포함된 페이지가 페이지를 제공하는 사이트와 동일한 경우에는 계속 사용할 수 있다.
            */
            .formLogin((formLogin) -> formLogin
                    .loginPage("/user/login")
                    .defaultSuccessUrl("/"))
            /*.formLogin 메서드는 스프링 시큐리티의 로그인 설정을 담당하는 부분으로
            로그인 페이지의 URL은 /user/login이고 로그인 성공시에 이동하는 디폴트 페이지는 루트 URL(/)임을 의미한다.*/
            .logout((logout) -> logout
                    .logoutRequestMatcher(new AntPathRequestMatcher("/user/logout"))
                    .logoutSuccessUrl("/")
                    .invalidateHttpSession(true));
                /*로그아웃 URL을 /user/logout으로 설정하고 로그아웃이 성공하면 루트(/) 페이지로 이동하도록 했다.
                그리고 로그아웃시 생성된 사용자 세션도 삭제하도록 처리했다.*/
    return http.build();
  }
}