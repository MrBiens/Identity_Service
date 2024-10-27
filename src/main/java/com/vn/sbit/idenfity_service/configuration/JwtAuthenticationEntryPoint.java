package com.vn.sbit.idenfity_service.configuration;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.vn.sbit.idenfity_service.dto.ApiResponse;
import com.vn.sbit.idenfity_service.exception.ErrorCode;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import java.io.IOException;
//class dieu huong neu security error bởi lỗi (403?) vì lỗi ở 1 tầng khác (security oauth2 config EntryPoint)
//được kích hoạt khi một yêu cầu không có thông tin xác thực hoặc có thông tin xác thực không hợp lệ
@Slf4j
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException)
            throws IOException, ServletException {
        log.error("UnAuthentication",authException);
        ErrorCode errorCode = ErrorCode.UNAUTHENTICATED;
        response.setStatus(errorCode.getHttpStatusCode().value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);//kiểu trả về

        ApiResponse<?> apiResponse=ApiResponse
                .builder()
                .code(errorCode.getCode())
                .message(errorCode.getMessage())
                .build();
        ObjectMapper objectMapper = new ObjectMapper();
        response.getWriter().write(objectMapper.writeValueAsString(apiResponse));
        response.flushBuffer();
    }
}
