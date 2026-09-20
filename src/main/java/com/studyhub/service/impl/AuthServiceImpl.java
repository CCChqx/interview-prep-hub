package com.studyhub.service.impl;

import com.studyhub.pojo.vo.AccessTokenResponse;
import com.studyhub.pojo.vo.LoginResponse;
import com.studyhub.service.AuthService;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

    @Override
    public LoginResponse login(String username, String password) {
        return null;
    }

    @Override
    public AccessTokenResponse refresh(String refreshToken) {
        return null;
    }

    @Override
    public void logout(Long userId) {

    }
}
