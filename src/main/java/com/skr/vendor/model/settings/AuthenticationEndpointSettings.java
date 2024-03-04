package com.skr.vendor.model.settings;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthenticationEndpointSettings {
    private String issuerUri;
    private String userInfoUri;
    private String jwkSetUri;

}
