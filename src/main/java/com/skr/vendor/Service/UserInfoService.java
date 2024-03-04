package com.skr.vendor.Service;

import com.skr.vendor.exceptions.ResourceNotFoundException;
import com.skr.vendor.model.UserInfo;
import com.skr.vendor.model.settings.AuthenticationEndpointSettings;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;
import java.util.Optional;

import static com.skr.vendor.constants.Constants.BEARER;

@Slf4j
@Service
public class UserInfoService implements IUserInfoService {

    private final WebClient.Builder webClientBuilder;
    private final AuthenticationEndpointSettings authenticationEndpointSettings;

    @Autowired
    public UserInfoService(WebClient.Builder webClientBuilder, AuthenticationEndpointSettings authenticationEndpointSettings) {
        this.webClientBuilder = webClientBuilder;
        this.authenticationEndpointSettings = authenticationEndpointSettings;
    }

    @Override
    public Optional<UserInfo> getUserInfo(String accessToken) {
        log.debug("Fetching user info with access token: {}", accessToken);

        WebClient webClient = buildWebClient(accessToken);
        try {
            Map<String, String> userInfoMap = webClient.get()
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            if (userInfoMap != null) {
                log.debug("User info retrieved successfully");
                return Optional.of(UserInfo.builder()
                        .givenName(userInfoMap.get("given_name"))
                        .familyName(userInfoMap.get("family_name"))
                        .vid(userInfoMap.get("custom:VID"))
                        .build());
            } else {
                log.warn("User info not found");
                return Optional.empty();
            }
        } catch (Exception e) {
            log.error("Error fetching user info: {}", e.getMessage());
            throw new ResourceNotFoundException("Error fetching user info", e);
        }
    }

    private WebClient buildWebClient(String accessToken) {
        String userInfoURL = authenticationEndpointSettings.getUserInfoUri();
        return webClientBuilder.baseUrl(userInfoURL)
                .defaultHeader(HttpHeaders.AUTHORIZATION, BEARER + accessToken)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }
}
