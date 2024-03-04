package com.skr.vendor.configuration.custom;

import com.skr.vendor.model.UserInfo;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.util.Collection;

public class CustomAuthentication
        extends JwtAuthenticationToken {

    private final UserInfo userInfo;

    public CustomAuthentication(
            Jwt jwt,
            Collection<? extends GrantedAuthority> authorities,
            UserInfo userInfo) {

        super(jwt, authorities);
        this.userInfo = userInfo;
    }

    public UserInfo getUserInfo() {
        return userInfo;
    }
}