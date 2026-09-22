package com.studyhub.controller;

import com.studyhub.common.Result;
import com.studyhub.pojo.dto.LoginRequest;
import com.studyhub.pojo.dto.RefreshTokenRequest;
import com.studyhub.exception.BusinessException;
import com.studyhub.pojo.dto.RegisterRequest;
import com.studyhub.pojo.vo.UserVO;
import com.studyhub.service.UserService;
import com.studyhub.util.JwtUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Tag(name = "登录鉴权",description = "登录与 token 签发")
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private UserService userService;

    @Operation(summary = "登录")
    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody LoginRequest req) {
        //校验账号密码（先写死，后换user表）
        if (!"admin".equals(req.getUsername()) || !"123456".equals(req.getPassword())){
            throw new BusinessException(401,"用户名或密码错误");
        }

        // 校验通过 → 签发 token （userId先用1L，表示admin）
        String access = jwtUtil.generateToken(1L,req.getUsername(),"access");
        String refresh = jwtUtil.generateToken(1L,req.getUsername(),"refresh");

        String redisKey = "studyhub:token:refresh:" + 1L;
        long remain = jwtUtil.parseToken(refresh).getExpiration().getTime() - System.currentTimeMillis();
        stringRedisTemplate.opsForValue().set(redisKey,refresh, Duration.ofMillis(remain));

        // 返回给前端
        Map<String, Object> data = new HashMap<>();
        data.put("username",req.getUsername());
        data.put("access",access);
        data.put("refresh",refresh);
        return Result.success(data);
    }

    //  新接口,放行
    @PostMapping("/refresh")
    public Result<String> refresh(@RequestBody RefreshTokenRequest req) {
        //  获取token
        Object r = req.getRefreshToken();
        if (r == null) {
            throw new BusinessException(400,"缺少 refresh参数");
        }
        String refresh = r.toString();
        //  解析refresh
        Claims claims;
        try{
            claims = jwtUtil.parseToken(refresh);
        }catch (ExpiredJwtException e){
            throw new BusinessException(401,"refresh已过期,请重新登录");
        }catch (JwtException | IllegalArgumentException e){
            throw new BusinessException(401,"refresh 无效");
        }
        //  校验type
        String type = claims.get("type", String.class);
        if (!"refresh".equals(type)) {
            throw new BusinessException(401,"token类型错误");
        }
        //  校验redis里是否含有token
        String refreshToken = stringRedisTemplate.opsForValue().get("studyhub:token:refresh:" + 1L);
        if (refreshToken == null || !refreshToken.equals(refresh)) {
            throw new BusinessException(401,"refresh 已失效/已登出,请重新获取");
        }
        //  返回access
        String username = claims.get("username", String.class);
        String access = jwtUtil.generateToken(1L, username, "access");
        return Result.success(access);
    }

    @Operation(summary = "注册")
    @PostMapping("/register")
    public Result<UserVO> register(@Valid @RequestBody RegisterRequest req) {
        return Result.success(userService.register(req));
    }
}
