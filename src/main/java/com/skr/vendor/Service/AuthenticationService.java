package com.skr.vendor.Service;

import com.skr.vendor.configuration.custom.CustomAuthentication;
import com.skr.vendor.model.UserInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class AuthenticationService implements IAuthenticationService {

    @Override
    public String getUserFullName() {
        log.debug("Retrieving user full name");
        Optional<UserInfo> userInfo = getUserInfo();
        if (userInfo.isPresent()) {
            return String.format("%s %s", userInfo.get().getGivenName()!=null?userInfo.get().getGivenName():"",
                    userInfo.get().getFamilyName()!=null?userInfo.get().getFamilyName():"");
        } else {
            log.error("User info is not available");
            throw new IllegalStateException("User info is not available");
        }
    }

    @Override
    public List<GrantedAuthority> getAuthorities() {
        log.debug("Retrieving user authorities");
        Authentication authentication = getAuthentication();
        return (List<GrantedAuthority>) authentication.getAuthorities();
    }

    private Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    private Optional<UserInfo> getUserInfo() {
        CustomAuthentication authentication = (CustomAuthentication) getAuthentication();
        UserInfo userInfo = authentication.getUserInfo();
        return Optional.of(userInfo);
    }
}
