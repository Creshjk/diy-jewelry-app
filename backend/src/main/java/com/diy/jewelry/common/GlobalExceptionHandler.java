package com.diy.jewelry.common;

import jakarta.validation.ConstraintViolationException;
import java.util.stream.Collectors;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ApiResponse<Void> handleBusiness(BusinessException ex) {
        return ApiResponse.fail(ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ApiResponse<Void> handleValid(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(item -> item.getField() + item.getDefaultMessage())
                .collect(Collectors.joining("；"));
        return ApiResponse.fail(message);
    }

    @ExceptionHandler({BindException.class, ConstraintViolationException.class})
    public ApiResponse<Void> handleBind(Exception ex) {
        return ApiResponse.fail(ex.getMessage());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ApiResponse<Void> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        String message = ex.getMostSpecificCause() == null ? "" : ex.getMostSpecificCause().getMessage();
        if (message.contains("Data too long for column")) {
            return ApiResponse.fail("保存失败，图片地址过长，请先扩容数据库字段后再试");
        }
        return ApiResponse.fail("保存失败，数据库数据校验未通过");
    }

    @ExceptionHandler(Exception.class)
    public ApiResponse<Void> handleOther(Exception ex) {
        return ApiResponse.fail("服务器处理失败，请稍后重试");
    }
}
