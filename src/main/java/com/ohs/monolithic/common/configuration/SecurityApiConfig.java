package com.ohs.monolithic.common.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ohs.monolithic.auth.exception.ApiAuthenticationFailureHandler;
import com.ohs.monolithic.auth.exception.ApiAuthenticationSuccessHandler;
import com.ohs.monolithic.auth.exception.ApiLogoutSuccessHandler;
import com.ohs.monolithic.auth.exception.CustomAuthenticationEntryPoint;
import com.ohs.monolithic.auth.filter.AuthenticationHeaderFilter;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.OAuth2LoginAuthenticationFilter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.context.SecurityContextHolderFilter;
import org.springframework.security.web.savedrequest.NullRequestCache;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Configuration
@RequiredArgsConstructor
public class SecurityApiConfig {
  private final ClientRegistrationRepository clientRegistrationRepository;
  @Value("${client.origin}")
  private String origin;

  @Value("${API_SERVER_URL}")
  private String apiServerUrl;

  final ObjectMapper objectMapper;
  @Bean
  @Order(1)
  SecurityFilterChain apiFilterChain(HttpSecurity http) throws Exception {

    http.cors(corsCustomizer -> corsCustomizer.configurationSource(new CorsConfigurationSource() {
      @Override
      public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(Collections.singletonList(origin));
        config.setAllowedMethods(Collections.singletonList("*"));
        config.setAllowCredentials(true);
        config.setAllowedHeaders(Collections.singletonList("*"));
        config.setMaxAge(3600L);
        return config;
      }
    }));

    RequestMatcher matcher = new AntPathRequestMatcher("/api/**");
    http
            .securityMatcher(matcher)
            // requestCache는 사용하지 않는다.
            .requestCache(config -> config.requestCache(new NullRequestCache()))
            .csrf((configurer) -> configurer.disable())
            .sessionManagement(
                    (configurer) -> configurer.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
            )
            .authorizeHttpRequests(
                    config -> config.requestMatchers(matcher).permitAll()
            )
            .exceptionHandling(config -> {
              config.authenticationEntryPoint(new CustomAuthenticationEntryPoint());
            })
            .formLogin(
                    config ->
                            config.loginProcessingUrl("/api/accounts/login")
                                    .usernameParameter("id")
                                    .passwordParameter("password")
                                    //.successHandler((request, response, authentication) -> {})
                                    .successHandler(new ApiAuthenticationSuccessHandler(objectMapper))
                                    .failureHandler(new ApiAuthenticationFailureHandler(objectMapper))
                                    .loginPage("/unused-login-page")
            )
            .oauth2Login(
                    config -> config.authorizationEndpoint(
                                    authEndpointConfig -> authEndpointConfig.baseUri("/api/oauth2/authorization")
                            )
                            //.loginPage("/unused-login-page")
                            .loginProcessingUrl("/api" + OAuth2LoginAuthenticationFilter.DEFAULT_FILTER_PROCESSES_URI)
                            .clientRegistrationRepository(apiRegistrationRepository())
                            .defaultSuccessUrl(origin)
            )
            .logout(config ->
                    config.logoutRequestMatcher(new AntPathRequestMatcher("/api/accounts/login", "DELETE"))
                            .logoutSuccessHandler(new ApiLogoutSuccessHandler())
            )
            .addFilterAfter(new AuthenticationHeaderFilter(), SecurityContextHolderFilter.class);


    return http.build();
  }

  ClientRegistrationRepository apiRegistrationRepository() {

    InMemoryClientRegistrationRepository casted = (InMemoryClientRegistrationRepository) this.clientRegistrationRepository;

    List<ClientRegistration> newRegistrations = new ArrayList<>();
    casted.forEach(registration -> {

      //  리다이렉트 URI만 변경
      ClientRegistration newRegistration = ClientRegistration.withRegistrationId(registration.getRegistrationId())
              .clientId(registration.getClientId())
              .clientSecret(registration.getClientSecret())
              .clientAuthenticationMethod(registration.getClientAuthenticationMethod())
              .authorizationGrantType(registration.getAuthorizationGrantType())
              .redirectUri(apiServerUrl+ "/api/login/oauth2/code/" + registration.getRegistrationId())
              .scope(registration.getScopes().toArray(new String[0]))
              .authorizationUri(registration.getProviderDetails().getAuthorizationUri())
              .tokenUri(registration.getProviderDetails().getTokenUri())
              .userInfoUri(registration.getProviderDetails().getUserInfoEndpoint().getUri())
              .userNameAttributeName(registration.getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName())
              .clientName(registration.getClientName())
              .build();
      newRegistrations.add(newRegistration);
    });
    return new InMemoryClientRegistrationRepository(newRegistrations);
  }

}
