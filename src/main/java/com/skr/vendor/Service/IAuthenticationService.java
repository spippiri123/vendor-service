package com.skr.vendor.Service;

import org.springframework.security.core.GrantedAuthority;

import java.util.List;

public interface IAuthenticationService {
    public String getUserFullName();

    public List<GrantedAuthority> getAuthorities();
}
