package com.vn.sbit.idenfity_service.exception;

import com.vn.sbit.idenfity_service.dto.ApiResponse;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintViolation;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.DefaultHandlerExceptionResolver;

import java.util.Map;
import java.util.Objects;

@ControllerAdvice
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class GlobalExceptionHandler {
    static String MIN_ATTRIBUTE = "min"; //min Validator


    //    static String MAX_ATTRIBUTE ="max";
    //ResponseEntity:một lớp trong Spring Framework được sử dụng để biểu diễn toàn bộ phản hồi HTTP
    @ExceptionHandler(value = Exception.class)
    ResponseEntity<ApiResponse> handlingRuntimeException(RuntimeException exception) {
        log.error("Exception: ", exception);
        ApiResponse apiResponse = new ApiResponse();

        apiResponse.setCode(ErrorCode.UNCATEGORIZED_EXCEPTION.getCode());
        apiResponse.setMessage(ErrorCode.UNCATEGORIZED_EXCEPTION.getMessage());

        return ResponseEntity.badRequest().body(apiResponse);
    }


    //xử lý ngoại lệ bằng class (AppException)
    @ExceptionHandler(value = AppException.class)
    ResponseEntity<ApiResponse> handlingAppException(AppException exception) {
        ApiResponse apiResponse = new ApiResponse();
        //AppException co construct ErrorCode
        ErrorCode errorCode = exception.getErrorCode();
        //code cua user_existed
        apiResponse.setCode(errorCode.getCode());
        //loi nhan error
        apiResponse.setMessage((errorCode.getMessage()));

        return ResponseEntity
                .status(errorCode.getHttpStatusCode())
                .body(apiResponse);
    }


    //user tạo không đúng quy định - user validation
    //  ConstraintViolation là một giao diện trong Java Bean Validation (JSR 380) được sử dụng để mô tả một vi phạm đối với một ràng buộc xác thực cụ thể trên một đối tượng
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    ResponseEntity<ApiResponse> handlingMethodArgumentNotValidException(MethodArgumentNotValidException exception) {
        String enumKey = exception.getFieldError().getDefaultMessage();//trả về message ở size UserCreationRequest
        ErrorCode errorCode = ErrorCode.INVALID_KEY; // defaul sẽ luôn là lỗi này, đề phòng trường hợp sai không có code phù hợp
        Map<String, Object> attributes = null; // tạo 1 biến kiểu map = null;
        try {
            errorCode = ErrorCode.valueOf(enumKey);//ép chuỗi string thành ErrorCode type
            //<T> not <A,T>
            var constraintViolation = exception.getBindingResult().getAllErrors().getFirst().unwrap(ConstraintViolation.class);//lấy lỗi đầu tiên chuyển sang Violation
            // Lấy các thuộc tính mô tả của ràng buộc - trả về type Map - KeyValue
            attributes = constraintViolation.getConstraintDescriptor().getAttributes();
            log.info(attributes.toString());

        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
        ApiResponse apiResponse = new ApiResponse();

        apiResponse.setCode(errorCode.getCode());
        apiResponse.setMessage(Objects.nonNull(attributes)
                ? MapAttribute(errorCode.getMessage(), attributes)
                : errorCode.getMessage());

        return ResponseEntity.badRequest().body(apiResponse);
    }

    String MapAttribute(String message, Map<String, Object> attributes) {
        String minValue = String.valueOf(attributes.get(MIN_ATTRIBUTE));// Lấy giá trị min từ key và gán value;  vd min = 18 -> minValue = 18
        return message.replace("{" + MIN_ATTRIBUTE + "}", minValue); // Thay thế {min} trong thông báo bằng giá trị cụ thể -> like @Pathvarible
    }

}
