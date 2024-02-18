package com.ohs.monolithic.configuration;

import com.ohs.monolithic.user.service.OAuth2AccountService;
import com.ohs.monolithic.utils.OAuth2ProviderIdExtractor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class OAuth2LoginConfig {




    final private ApplicationContext applicationContext;

    @Bean
    public Map<String, OAuth2ProviderIdExtractor> providerIdExtractors() {
        Map<String, OAuth2ProviderIdExtractor> extractors = new HashMap<>();
        Map<String, OAuth2ProviderIdExtractor> beansOfType = applicationContext.getBeansOfType(OAuth2ProviderIdExtractor.class);
        for (Map.Entry<String, OAuth2ProviderIdExtractor> entry : beansOfType.entrySet()) {
            OAuth2ProviderIdExtractor extractor = entry.getValue();

            String providerName = extractor.getProviderName();
            extractors.put(providerName, extractor);
        }
        return extractors;
    }




}
