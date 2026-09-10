package com.studyhub.Fliter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Component
public class TraceIdFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        // 生成 traceId(16位,去横线)
        String traceId = UUID.randomUUID().toString().replace("-","").substring(0,16);
        MDC.put("traceId",traceId);   //  放入MDC (线程级上下文)
        try{
            filterChain.doFilter(request,response);  //  继续走后续 (拦截器/Controller/Service)
        }finally {
            MDC.remove("traceId");  //  请求结束必须清除
        }
    }
}
