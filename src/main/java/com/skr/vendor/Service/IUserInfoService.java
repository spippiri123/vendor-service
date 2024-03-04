package com.skr.vendor.Service;

import com.skr.vendor.model.UserInfo;

import java.util.Optional;

public interface IUserInfoService {

    public Optional<UserInfo> getUserInfo(String accessToken);
}
