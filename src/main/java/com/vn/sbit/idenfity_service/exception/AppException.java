package com.vn.sbit.idenfity_service.exception;

public class AppException extends RuntimeException{
    private ErrorCode errorCode;


    public AppException(ErrorCode errorCode) {
        //super ( lấy tham số ở Construct từ lớp cha)
        super(errorCode.getMessage()); //truyền lời nhắn lỗi tới RuntimeException . Message
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(ErrorCode errorCode) {
        this.errorCode = errorCode;
    }

}
