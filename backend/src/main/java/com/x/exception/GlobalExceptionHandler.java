package com.x.exception;

import com.x.utils.Result;
import com.x.exception.BusinessException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 处理业务异常
    @ExceptionHandler(BusinessException.class)
    public Result<?> handleBusinessException(BusinessException e) {
        return Result.error(e.getMessage());
    }

    // 处理其他异常
    @ExceptionHandler(Exception.class)
    public Result<?> handleException(Exception e) {
        System.out.println("服务器发生异常：" + e.getMessage());
        e.printStackTrace();
        return Result.error("服务器开小差了，请稍后再试");
    }
}
