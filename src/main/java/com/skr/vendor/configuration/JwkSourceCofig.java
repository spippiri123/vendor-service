package com.skr.vendor.configuration;

import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.jwk.source.RemoteJWKSet;
import com.nimbusds.jose.proc.SecurityContext;
import com.skr.vendor.model.settings.AuthenticationEndpointSettings;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;

@Configuration
@RequiredArgsConstructor
public class JwkSourceCofig {
    private final AuthenticationEndpointSettings authenticationEndpointSettings;

    @Bean
    public JWKSource<SecurityContext> getJWKSource() throws MalformedURLException, URISyntaxException {
        return new RemoteJWKSet<>(URI.create(authenticationEndpointSettings.getJwkSetUri()).toURL());
    }
}
