package com.studyhub.service.impl;

import com.studyhub.converter.UserConverter;
import com.studyhub.exception.BusinessException;
import com.studyhub.pojo.entity.User;
import com.studyhub.pojo.vo.AccessTokenResponse;
import com.studyhub.pojo.vo.LoginResponse;
import com.studyhub.service.AuthService;
import com.studyhub.service.UserService;
import com.studyhub.util.JwtUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Service
public class AuthServiceImpl implements AuthService {

    private static final String REFRESH_TOKEN_PREFIX =
            "studyhub:token:refresh:";

    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final StringRedisTemplate stringRedisTemplate;

    public AuthServiceImpl(UserService userService,
                           JwtUtil jwtUtil,
                           StringRedisTemplate stringRedisTemplate) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
        this.stringRedisTemplate = stringRedisTemplate;
    }

    /**
     * 登录流程：
     *
     * 1. UserService 负责查询用户、BCrypt 校验密码、检查状态
     * 2. JwtUtil 负责生成 access / refresh token
     * 3. AuthService 负责把 refresh token 写入 Redis
     * 4. 把 User 转成 UserVO，组装 LoginResponse
     */
    @Override
    public LoginResponse login(String username, String password) {
        User user = userService.authenticate(username, password);

        String accessToken = jwtUtil.generateToken(
                user.getId(),
                user.getUsername(),
                "access"
        );

        String refreshToken = jwtUtil.generateToken(
                user.getId(),
                user.getUsername(),
                "refresh"
        );

        String redisKey = refreshKey(user.getId());

        long remain = jwtUtil.parseToken(refreshToken)
                .getExpiration()
                .getTime() - System.currentTimeMillis();

        stringRedisTemplate.opsForValue().set(
                redisKey,
                refreshToken,
                Duration.ofMillis(remain)
        );

        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setAccessToken(accessToken);
        loginResponse.setRefreshToken(refreshToken);
        loginResponse.setUser(UserConverter.toVO(user));
        return loginResponse;
    }

    /**
     * 刷新流程：
     *
     * 1. 解析 refresh token
     * 2. 校验签名和过期时间
     * 3. 校验 type 必须是 refresh
     * 4. 从 sub 中获取真实 userId
     * 5. 校验 Redis 中保存的是否是同一个 refresh token
     * 6. 校验用户是否存在且状态正常
     * 7. 生成新的 access token
     */
    @Override
    public AccessTokenResponse refresh(String refreshToken) {

        if (refreshToken == null || refreshToken.isBlank()) {
            throw new BusinessException(400,"refreshToken 不能为空");
        }

        Claims claims;

        try{
            claims = jwtUtil.parseToken(refreshToken);
        }catch (ExpiredJwtException e){
            throw new BusinessException(401,"refreshToken 已过期,请重新登录");
        }catch (JwtException | IllegalArgumentException e){
            throw new BusinessException(401,"refresh 无效");
        }

        String type = claims.get("type",String.class);

        if (!"refresh".equals(type)) {
            throw new BusinessException(401,"token 类型错误");
        }

        Long userId;

        try{
            userId = Long.valueOf(claims.getSubject());
        }catch (NumberFormatException e){
            throw new BusinessException(401,"refresh 无效");
        }

        User user = userService.getUserById(userId);

        if (user == null || Integer.valueOf(0).equals(user.getStatus())) {
            throw new BusinessException(401,"用户已不存在或已失效");
        }

        String savedToken = stringRedisTemplate.opsForValue()
                .get(refreshKey(userId));

        if (savedToken == null || !savedToken.equals(refreshToken)) {
            throw new BusinessException(401,"refresh 已失效,请重新登录");
        }

        Long remain = stringRedisTemplate.getExpire(refreshKey(userId), TimeUnit.MILLISECONDS);

        stringRedisTemplate.opsForValue().set(
                refreshKey(userId),
                jwtUtil.generateToken(userId,user.getUsername(),"refresh"),
                Duration.ofMillis(remain));

        String accessToken = jwtUtil.generateToken(
                user.getId(),
                user.getUsername(),
                "access"
        );

        AccessTokenResponse response = new AccessTokenResponse();
        response.setAccessToken(accessToken);

        return response;
    }

    /**
     * 登出时删除 refresh token。
     *
     * 注意：已经签发的 access token 在过期前仍然有效，
     * 这是 JWT 无状态设计的正常取舍。
     */
    @Override
    public void logout(Long userId) {
        stringRedisTemplate.delete(refreshKey(userId));
    }


    private String refreshKey(Long userId) {
        return REFRESH_TOKEN_PREFIX + userId;
    }
}
