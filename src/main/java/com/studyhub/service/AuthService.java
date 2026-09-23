package com.studyhub.service;

import com.studyhub.pojo.vo.AccessTokenResponse;
import com.studyhub.pojo.vo.LoginResponse;

public interface AuthService {

    LoginResponse login(String username,String password);

    AccessTokenResponse refresh(String refreshToken);

    void logout(Long userId);
}
