package com.skr.vendor.util;

import com.skr.vendor.model.UserInfo;

public class TestDataUtil {

    public static UserInfo getUserInfo() {
        UserInfo userInfo = UserInfo.builder().givenName("Skr").familyName("pip").vid("123").build();
        return userInfo;
    }
}
