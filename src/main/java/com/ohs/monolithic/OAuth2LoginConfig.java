package com.ohs.monolithic;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.oauth2.client.OAuth2LoginConfigurer;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

@Component
@RequiredArgsConstructor
public class OAuth2LoginConfig {

    private final OAuth2UserService uService;
    public void Apply(HttpSecurity http) throws Exception {
        http.oauth2Login(oauth2Configurer -> oauth2Configurer
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
            DefaultOAuth2User defaultOAuth2User = (DefaultOAuth2User) authentication.getPrincipal();
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
