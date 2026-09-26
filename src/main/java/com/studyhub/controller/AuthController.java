package com.studyhub.controller;

import com.studyhub.common.Result;
import com.studyhub.pojo.dto.LoginRequest;
import com.studyhub.pojo.dto.RefreshTokenRequest;
import com.studyhub.pojo.dto.RegisterRequest;
import com.studyhub.pojo.vo.AccessTokenResponse;
import com.studyhub.pojo.vo.LoginResponse;
import com.studyhub.pojo.vo.UserVO;
import com.studyhub.service.AuthService;
import com.studyhub.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Tag(name = "登录鉴权",description = "登录与 token 签发")
@RestController
@RequestMapping("/api/auth")
public class AuthController {


    @Autowired
    private AuthService authService;

    @Autowired
    private UserService userService;

    @Operation(summary = "注册")
    @PostMapping("/register")
    public Result<UserVO> register(@Valid @RequestBody RegisterRequest registerRequest) {
        return Result.success(userService.register(registerRequest));
    }

    @Operation(summary = "登录")
    @PostMapping("/login")
    public Result<LoginResponse> login(
            @Valid @RequestBody LoginRequest req) {
        return Result.success(
                authService.login(
                        req.getUsername(),
                        req.getPassword()
                )
        );
    }

    //  新接口,放行
    @PostMapping("/refresh")
    public Result<AccessTokenResponse> refresh(
            @Valid @RequestBody RefreshTokenRequest req) {
        return Result.success(
                authService.refresh(req.getRefreshToken())
        );
    }

    @PostMapping("/logout")
    public Result<Void> logout(
            @RequestAttribute("userId")  Long userId){

        authService.logout(userId);
        return Result.success();
    }
}
