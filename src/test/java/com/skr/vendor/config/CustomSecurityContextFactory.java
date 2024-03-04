package com.skr.vendor.config;

import com.skr.vendor.configuration.custom.CustomAuthentication;
import com.skr.vendor.model.UserInfo;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class CustomSecurityContextFactory
        implements WithSecurityContextFactory<WithCustomUser> {

    @Override
    public SecurityContext createSecurityContext(
            WithCustomUser withCustomUser) {
        SecurityContext context =
                SecurityContextHolder.createEmptyContext();

//        Jwt jwt = ((JwtAuthenticationToken) SecurityContextHolder.getContext().getAuthentication()).getToken();
        Jwt jwt = Jwt.withTokenValue("sampleJwtToken")
                .header("alg", "HS256")
                .claim("sub", "user123")
                .claim("authorities", Collections.singletonList("ROLE_USER"))
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(3600))
                .build();
        List<SimpleGrantedAuthority> authorityList = Arrays.asList(withCustomUser.authorites()).stream().
                map(SimpleGrantedAuthority::new).collect(Collectors.toList());

        UserInfo userInfo = UserInfo.builder().givenName(withCustomUser.givenname())
                .familyName(withCustomUser.familyname())
                .vid(withCustomUser.vid()).build();

        CustomAuthentication customAuthentication = new CustomAuthentication(jwt, authorityList, userInfo);

        context.setAuthentication(customAuthentication);

        return context;
    }
}