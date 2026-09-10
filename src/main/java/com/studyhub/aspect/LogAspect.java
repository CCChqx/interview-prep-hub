package com.studyhub.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect // 切面声明
@Component
public class LogAspect {

    //  切入点 ：controller 包下所有方法
    @Pointcut("execution(* com.studyhub.controller..*.*(..))")
    public void controllerPointcut() {}

    //  环绕通知 ：方法执行前后各干一件事(算耗时)
    @Around("controllerPointcut()")
    public Object logTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        try{
            return joinPoint.proceed();  // 执行真正的目标方法
        }finally {
            long cost = System.currentTimeMillis() - start;
            log.info("接口{} 耗时{} ms",joinPoint.getSignature().toShortString(),cost);
        }
    }
}
