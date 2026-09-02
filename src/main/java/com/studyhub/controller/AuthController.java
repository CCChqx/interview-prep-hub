package com.studyhub.controller;

import com.studyhub.common.Result;
import com.studyhub.dto.LoginRequest;
import com.studyhub.exception.BusinessException;
import com.studyhub.util.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@Tag(name = "登录鉴权",description = "登录与 token 签发")
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private JwtUtil jwtUtil;

    @Operation(summary = "登录")
    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody LoginRequest req) {
        //校验账号密码（先写死，后换user表）
        if (!"admin".equals(req.getUsername()) || !"123456".equals(req.getPassword())){
            throw new BusinessException(401,"用户名或密码错误");
        }

        // 校验通过 → 签发 token （userId先用1L，表示admin）
        String token = jwtUtil.generateToken(1L,req.getUsername());

        // 返回给前端
        Map<String, Object> data = new HashMap<>();
        data.put("token",token);
        data.put("username",req.getUsername());
        return Result.success(data);
    }
}
