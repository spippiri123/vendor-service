package com.skr.vendor.configuration;

import com.skr.vendor.model.settings.AuthenticationEndpointSettings;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class ConfigProperties {
    @Bean
    @ConfigurationProperties(prefix = "spring.oauth2.resourceserver.jwt")
    public AuthenticationEndpointSettings authenticationEndpointSettings() {
        return new AuthenticationEndpointSettings();
    }
}
