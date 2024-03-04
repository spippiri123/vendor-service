package com.skr.vendor.configuration.custom;

import com.skr.vendor.Service.IUserInfoService;
import com.skr.vendor.model.UserInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Component
public class CustomJwtAuthenticationConverter implements Converter<Jwt, CustomAuthentication> {

    private final IUserInfoService userInfoService;

    public CustomJwtAuthenticationConverter(IUserInfoService userInfoService) {
        this.userInfoService = userInfoService;
    }

    @Override
    public CustomAuthentication convert(Jwt jwt) {
        log.debug("Converting JWT to AuthenticationToken");


        checkAndAllowIfAccessToken(jwt);

        Collection<GrantedAuthority> authorities = parseScopes(jwt);
        authorities.addAll(parseCognitoGroups(jwt));
        Optional<UserInfo> userInfo = userInfoService.getUserInfo(jwt.getTokenValue());


        log.debug("JWT converted successfully to AuthenticationToken");
        return new CustomAuthentication(jwt,
                Collections.unmodifiableCollection(authorities),
                userInfo.orElse(null));

    }

    private void checkAndAllowIfAccessToken(Jwt jwt) {
        Map<String, Object> claims = jwt.getClaims();

        boolean isAccessTokenClaimsExists = Stream.of("scope", "client_id")
                .allMatch(claims::containsKey);

        if (!isAccessTokenClaimsExists) {
            log.warn("Invalid JWT: Missing required claims, it is not an access Token");
            throw new BadCredentialsException("Invalid JWT: Missing required claims");
        }
    }


    private Collection<GrantedAuthority> parseCognitoGroups(Jwt jwt) {
        log.debug("Parsing cognito groups from JWT");
        List<String> groups = jwt.getClaimAsStringList("cognito:groups");
        if (groups == null) {
            return Collections.emptyList();
        }

        return groups.stream()
                .map(c -> String.format("CGROUP_%s", c))
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    private Collection<GrantedAuthority> parseScopes(Jwt jwt) {
        log.debug("Parsing scopes from JWT");
        String scopeClaim = jwt.getClaimAsString("scope");
        if (scopeClaim == null) {
            return Collections.emptyList();
        }
        return Arrays.stream(scopeClaim.split(" "))
                .map(s -> String.format("SCOPE_%s", s))
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }
}