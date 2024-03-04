package com.skr.vendor.controller;

import com.skr.vendor.Service.IAuthenticationService;
import com.skr.vendor.model.VendorAuthResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.skr.vendor.constants.Constants.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = REST_BINDING)
@Slf4j
public class VendorController {

    private final IAuthenticationService authenticationService;

    //@GetMapping("/demo")
    public Authentication demo(Authentication a) {

        log.info("demo");
        return a;
    }

    @GetMapping(UNAUTHENTICATED)
    public ResponseEntity<VendorAuthResponse> unauthenticated() {
        log.debug("Unauthenticated endpoint accessed");
        VendorAuthResponse vendorAuthResponse = VendorAuthResponse.builder()
                .message("Free for all to see")
                .name("Anonymous").build();
        return ResponseEntity.ok(vendorAuthResponse);
    }

    @GetMapping(AUTHENTICATED)
    public ResponseEntity<VendorAuthResponse> authenticated() {
        log.debug("Authenticated endpoint accessed");
        VendorAuthResponse vendorAuthResponse = VendorAuthResponse.builder()
                .message("Looks like you have been authenticated")
                .grantedAuthorities(authenticationService.getAuthorities())
                .name(authenticationService.getUserFullName()).build();
        return ResponseEntity.ok(vendorAuthResponse);
    }

    @GetMapping(VENDORS)
    @PreAuthorize("hasAuthority('CGROUP_VENDOR')")
    public ResponseEntity<VendorAuthResponse> vendors() {
        log.debug("Vendors endpoint accessed");
        VendorAuthResponse vendorAuthResponse = VendorAuthResponse.builder()
                .message("Welcome to the vendor group!")
                .grantedAuthorities(authenticationService.getAuthorities())
                .name(authenticationService.getUserFullName()).build();
        return ResponseEntity.ok(vendorAuthResponse);
    }

    @GetMapping(VENDORS_VID)
    @PreAuthorize("hasAuthority('CGROUP_VENDOR') and #authentication.userInfo.vid==#vid")
    public ResponseEntity<VendorAuthResponse> specificVendor(@PathVariable String vid, @Qualifier("customAuthentication") Authentication authentication) {
        log.debug("Vendors with VID endpoint accessed");
        VendorAuthResponse vendorAuthResponse = VendorAuthResponse.builder()
                .message("Looks like you're a specific vendor")
                .grantedAuthorities(authenticationService.getAuthorities())
                .vid(vid)
                .name(authenticationService.getUserFullName()).build();
        return ResponseEntity.ok(vendorAuthResponse);
    }
}

