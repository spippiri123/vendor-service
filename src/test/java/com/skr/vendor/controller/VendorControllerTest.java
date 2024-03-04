package com.skr.vendor.controller;

import com.skr.vendor.Service.IAuthenticationService;
import com.skr.vendor.Service.IUserInfoService;
import com.skr.vendor.config.WithCustomUser;
import com.skr.vendor.model.UserInfo;
import com.skr.vendor.util.TestDataUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.MediaType;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.skr.vendor.constants.Constants.*;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ComponentScan(basePackages = "com.skr.vendor")
public class VendorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IUserInfoService userInfoService;

    @MockBean
    private IAuthenticationService authenticationService;

    @Test
    public void testUnAuthenticatedEndpoint_withoutAccessToken_shouldReturnSuccess() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(REST_BINDING + UNAUTHENTICATED)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Free for all to see"))
                .andExpect(jsonPath("$.name").value("Anonymous"));
    }

    @Test
    public void testAuthenticatedEndpoint_withAccessToken_shouldReturnSuccess() throws Exception {

        List<GrantedAuthority> authorityList = new ArrayList<>();
        authorityList.add(new GrantedAuthority() {
            @Override
            public String getAuthority() {
                return "CGROUP_VENDOR";
            }
        });

        when(userInfoService.getUserInfo(anyString())).thenReturn(Optional.of(TestDataUtil.getUserInfo()));
        when(authenticationService.getAuthorities()).thenReturn(authorityList);
        when(authenticationService.getUserFullName()).thenReturn("skr pip");


        mockMvc.perform(MockMvcRequestBuilders.get(REST_BINDING + AUTHENTICATED)
                        .with(jwt().authorities(() -> "CGROUP_VENDOR")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Looks like you have been authenticated"))
                .andExpect(jsonPath("$.name").value("skr pip"));
    }

    @Test
    public void testAuthenticatedEndpoint_withoutAccessToken_shouldReturnUnauthorize() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(REST_BINDING + AUTHENTICATED))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testVendorsEndpoint_withAccessTokenAndVendorGroup_shouldReturnSuccess() throws Exception {


        List<GrantedAuthority> authorityList = new ArrayList<>();
        authorityList.add(new GrantedAuthority() {
            @Override
            public String getAuthority() {
                return "CGROUP_VENDOR";
            }
        });

        when(userInfoService.getUserInfo(anyString())).thenReturn(Optional.of(TestDataUtil.getUserInfo()));
        when(authenticationService.getAuthorities()).thenReturn(authorityList);
        when(authenticationService.getUserFullName()).thenReturn("skr pip");


        mockMvc.perform(MockMvcRequestBuilders.get(REST_BINDING + VENDORS)
                        .with(jwt().authorities(() -> "CGROUP_VENDOR")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Welcome to the vendor group!"))
                .andExpect(jsonPath("$.name").value("skr pip"))
                .andExpect(jsonPath("$.grantedAuthorities[*].authority", containsInAnyOrder("CGROUP_VENDOR")));
    }


    @Test
    public void testVendorsEndpoint_withAccessTokenAndNonVendorGroup_shouldReturnForbidden() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.get(REST_BINDING + VENDORS)
                        .with(jwt().authorities(() -> "CGROUP_VENDOR1")))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithCustomUser(authorites = {"CGROUP_VENDOR1"}, givenname = "shekar", familyname = "pip", vid = "2cc40d4d-36c7-4c60-b15c-761dded8abb5")
    public void testVendorSpecificEndpoint_withNonVendorGroup_shouldReturnForbidden() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(REST_BINDING + VENDORS_VID, "2cc40d4d-36c7-4c60-b15c-761dded8abb5"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithCustomUser(authorites = {"CGROUP_VENDOR"}, givenname = "shekar", familyname = "pip", vid = "2cc40d4d-36c7-4c60-b15c-761dded8abb4")
    public void testVendorSpecificEndpoint_withVendorGroupAndwrongVid_shouldReturnForbidden() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(REST_BINDING + VENDORS_VID, "2cc40d4d-36c7-4c60-b15c-761dded8abb5"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithCustomUser(authorites = {"CGROUP_VENDOR"}, givenname = "shekar", familyname = "pip", vid = "2cc40d4d-36c7-4c60-b15c-761dded8abb5")
    public void testVendorSpecificEndpoint_withVendorGroupAndValidVid_shouldReturnSuccess() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(REST_BINDING + VENDORS_VID, "2cc40d4d-36c7-4c60-b15c-761dded8abb5"))
                .andExpect(status().isOk());
    }


    @Test
    @WithCustomUser(authorites = {"CGROUP_VENDOR"}, givenname = "shekar", familyname = "pip", vid = "2cc40d4d-36c7-4c60-b15c-761dded8abb5")
    void testVendorSpecificEndpoint_withVendorGroupAndValidVid_shouldReturnValidResponse() throws Exception {
        // Mock authentication service response
        when(authenticationService.getAuthorities()).thenReturn(List.of(new SimpleGrantedAuthority("CGROUP_VENDOR")));
        when(authenticationService.getUserFullName()).thenReturn("shekar pip");
        String vid = "2cc40d4d-36c7-4c60-b15c-761dded8abb5";

        // Perform GET request to the endpoint
        mockMvc.perform(MockMvcRequestBuilders.get(REST_BINDING + VENDORS_VID, vid)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Looks like you're a specific vendor"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.grantedAuthorities").isNotEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("$.vid").value(vid))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("shekar pip"));
    }

}

