package com.ohs.monolithic.common.configuration;

import com.ohs.monolithic.account.service.OAuth2AccountService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.header.writers.frameoptions.XFrameOptionsHeaderWriter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;


@Configuration
/*@EnableWebSecurity(debug = true)*/
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor

public class SecurityConfig {

    //private final OAuth2LoginConfig oAuthConfigurer;


    @Bean
    @Order(2)
     SecurityFilterChain webFilterChain(HttpSecurity http) throws Exception {

      this.Apply(http);
        http
                .authorizeHttpRequests((authorizeHttpRequests) -> authorizeHttpRequests
                        .requestMatchers(new AntPathRequestMatcher("/**")).permitAll())
                //위는 모든 인증되지 않은 요청을 허락한다는 의미이다. 따라서 로그인을 하지 않더라도 모든 페이지에 접근할 수 있다.

                .csrf((csrf) -> csrf
                        .ignoringRequestMatchers(new AntPathRequestMatcher("/h2-console/**")))
                // /h2-console/로 시작하는 URL은 CSRF 검증을 하지 않는다는 설정을 추가했다.

                .headers((headers) -> headers
                        .addHeaderWriter(new XFrameOptionsHeaderWriter(
                                XFrameOptionsHeaderWriter.XFrameOptionsMode.SAMEORIGIN)))
                /*
                사이트의 콘텐츠가 다른 사이트에 포함되지 않도록 하기 위해 X-Frame-Options 헤더값을 사용하여 이를 방지한다. (clickjacking 공격을 막기위해 사용함)
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
                        .invalidateHttpSession(true))
                /*로그아웃 URL을 /user/logout으로 설정하고 로그아웃이 성공하면 루트(/) 페이지로 이동하도록 했다.
                그리고 로그아웃시 생성된 사용자 세션도 삭제하도록 처리했다.*/
        ;
        return http.build();

    }

  /*@Bean
  @Order(1)
  public SecurityFilterChain jwtFilterChain(HttpSecurity http) throws Exception {

    // OAuth2 관련 URL 매칭을 위한 AntPathRequestMatcher
    *//*RequestMatcher oauth2Matcher = new AntPathRequestMatcher("/oauth2/**");
    RequestMatcher oauth2Matcher2 = new AntPathRequestMatcher("/login/oauth2/**");*//*

    RequestMatcher oauth2Matcher = new AntPathRequestMatcher("test/oauth2/**");
    RequestMatcher oauth2Matcher2 = new AntPathRequestMatcher("test/login/oauth2/**");

    // Authorization 헤더에 Bearer 토큰이 포함된 요청을 매칭하기 위한 커스텀 RequestMatcher
    RequestMatcher bearerTokenMatcher = new RequestMatcher() {
      @Override
      public boolean matches(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        return (authHeader != null && authHeader.startsWith("Bearer "));
      }
    };

    // OrRequestMatcher를 사용하여 두 매처를 결합
    RequestMatcher combinedMatcher = new OrRequestMatcher(oauth2Matcher, oauth2Matcher2, bearerTokenMatcher);

    // 결합된 매처를 사용하여 securityMatcher 설정
    http.securityMatcher(combinedMatcher)
        .httpBasic(configurer -> configurer.disable())
        .csrf((csrfConfig) -> csrfConfig.disable())
        .sessionManagement(configurer -> configurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS))


    ;

    //oAuthConfigurer.Apply(http);
             // JWT 검증 설정
    return http.build();
  }*/

  private final OAuth2AccountService uService;
  public void Apply(HttpSecurity http) throws Exception {
    http
            .oauth2Login(
                    oauth2Configurer -> oauth2Configurer
                            .userInfoEndpoint(userInfoEndpoint  -> userInfoEndpoint.userService(this.uService))
                            .loginPage("/user/login")
                            .successHandler(successHandler())
            );

    System.out.println("apply oauth2loginConfig");
  }
  @Bean
  AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
    System.out.println("apply authenticationManager");
    return authenticationConfiguration.getAuthenticationManager();
  }

    /*AuthenticationManager 빈을 생성했다. AuthenticationManager는 스프링 시큐리티의 인증을 담당한다.
    AuthenticationManager는 사용자 인증시 앞에서 작성한 UserSecurityService와 PasswordEncoder를 사용한다.*/

  public AuthenticationSuccessHandler successHandler() {
    return ((request, response, authentication) -> {

      System.out.println("success handler");
      //DefaultOAuth2User defaultOAuth2User = (DefaultOAuth2User) authentication.getPrincipal();
      response.sendRedirect("/");
            /*String id = defaultOAuth2User.getAttributes().get("id").toString();
            String body = """
                    {"id":"%s"}
                    """.formatted(id);

            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setCharacterEncoding(StandardCharsets.UTF_8.name());

            PrintWriter writer = response.getWriter();
            writer.println(body);
            writer.flush();*/
    });
  }






}