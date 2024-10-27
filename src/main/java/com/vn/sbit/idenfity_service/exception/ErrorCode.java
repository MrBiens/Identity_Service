package com.vn.sbit.idenfity_service.exception;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
public enum ErrorCode {
    UNCATEGORIZED_EXCEPTION(9999,"UNCATEGORIZED_EXCEPTION", HttpStatus.INTERNAL_SERVER_ERROR),//500:lỗi tại server
    INVALID_KEY(1000,"Invalid message key",HttpStatus.BAD_REQUEST), //khi error.code gặp lỗi(không được tìm thấy,....)
    USER_EXISTED(1001,"User existed",HttpStatus.BAD_REQUEST ),//400 : yêu cầu không hợp lệ
    USER_USERNAME_INVALID(1002,"Username must be at least {min} character",HttpStatus.BAD_REQUEST),
    USER_PASSWORD_INVALID(1003,"Password must be at least {min} character",HttpStatus.BAD_REQUEST),
    USER_NOT_EXISTED(1004,"User not existed" ,HttpStatus.NOT_FOUND),//404 không tìm thấy
    INVALID_DOB(1005,"Your age must be at least {min}.",HttpStatus.BAD_REQUEST),
    UNAUTHENTICATED(2001,"Unauthenticated",HttpStatus.UNAUTHORIZED),//401 -người dùng không được xác thực -> vì sai thông tin
    UNAUTHORIZED(2002,"Unauthorized.Please check your Role",HttpStatus.FORBIDDEN), //403 từ chối - không có quyền truy cập vd user-> admin
    ROLES_NOT_FOUND(2000,"Role Not Found",HttpStatus.NOT_FOUND),
    ;

      int code;
      String message;
      HttpStatusCode httpStatusCode;


    ErrorCode(int code , String message,HttpStatusCode httpStatusCode) {
        this.code = code;
        this.message = message;
        this.httpStatusCode=httpStatusCode;
    }



}
