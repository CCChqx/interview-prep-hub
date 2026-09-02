package com.studyhub.config;

import com.studyhub.util.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class JwtInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 获取 Authorization请求头
        String auth = request.getHeader("Authorization");

        // 没带，或不是Bearer开头 → 401
        if (auth == null || !auth.startsWith("Bearer ")) {
            return reject(response,"未登录或token缺失");
        }

        // 剥掉 "Bearer" 前缀（7个字符），只留token
        String token = auth.substring(7);

        // 解签 + 验过期（失败抛异常）
        try{
            Claims claims = jwtUtil.parseToken(token);
            // 把userId /username 放进 request，供后面的Controller用
            request.setAttribute("userId",claims.getSubject());
            request.setAttribute("username",claims.get("username"));
            return  true; //放行
        }catch (Exception e){
            return reject(response,"token 无效或已过期");
        }
    }

    private boolean reject(HttpServletResponse response, String msg) throws Exception {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write("{\"code\":401,\"message\":\"" + msg + "\",\"data\":null}");
        return false;
    }
}
