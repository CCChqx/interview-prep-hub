package com.studyhub.aspect;

import com.studyhub.annotation.Idempotent;
import com.studyhub.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Arrays;

@Slf4j
@Aspect
@Component
public class IdempotentAspect {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Around("@annotation(idempotent)")
    public Object around(ProceedingJoinPoint joinPoint, Idempotent idempotent) throws Throwable {
       //  防两次请求,第一次请求设置标记,第二次查找标记是否存在
        String key = "idem:" + idempotent.value() + ":" + joinPoint.getSignature().toShortString() + Arrays.hashCode(joinPoint.getArgs());

        Boolean first = false;
        try{
            first = stringRedisTemplate.opsForValue().setIfAbsent(key,"1",Duration.ofSeconds(idempotent.timeout()));
        }catch (Exception e){
            //  Redis挂了 → 降级放行:防重不能拖垮业务
            log.warn("幂等检查异常,降级放行,key={}",key,e);
            return joinPoint.proceed();
        }

        // 拦截重复请求
        if (!Boolean.TRUE.equals(first)){
            throw new BusinessException(429,"请勿重复提交");
        }

        //  第一次请求:放行执行原方法
        try{
            return joinPoint.proceed();
        }catch (Exception e){
            //  业务失败 → 删key,允许用户立即重试
            stringRedisTemplate.delete(key);
            throw e;
        }
    }

}
