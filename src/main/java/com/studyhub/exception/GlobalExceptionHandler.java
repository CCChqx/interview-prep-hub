package com.studyhub.exception;

import com.studyhub.common.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // 接业务异常
    @ExceptionHandler(BusinessException.class)
    public Result<Void> handleBusinessException(BusinessException e) {
        log.warn(e.getMessage());
        return Result.error(e.getCode(),e.getMessage());
    }

    // 接校验参数异常
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<Void> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        String message = "参数校验失败";
        if(e.getBindingResult().getFieldErrors().size()>0){
            message = e.getBindingResult().getFieldErrors().get(0).getDefaultMessage();
        }
        return Result.error(400,message);
    }

    // 兜底 (其他所有异常，防止堆栈泄露给前端)
    @ExceptionHandler(Exception.class)
    public Result<Void> handleOther(Exception e) {
        log.error("系统异常");
        return Result.error(500,"系统繁忙，请稍后重试");
    }
}
