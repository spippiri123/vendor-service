package com.skr.vendor.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import org.springframework.security.core.GrantedAuthority;

import java.util.List;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record VendorAuthResponse(
        String message,
        String name,
        List<GrantedAuthority> grantedAuthorities,
        String vid
) {
}
