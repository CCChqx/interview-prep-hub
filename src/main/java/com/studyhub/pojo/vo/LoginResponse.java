package com.studyhub.pojo.vo;

import lombok.Data;

@Data
public class LoginResponse {
    private String accessToken;
    private String refreshToken;
    private UserVO user;
}
