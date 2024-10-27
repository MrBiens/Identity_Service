package com.vn.sbit.idenfity_service.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;

//thong bao cho json biet rang nhung request body nao null thi se khong hien thi vao
//chuan hoa
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data//lombok -Thay cho getter , setter , tostring.....
@NoArgsConstructor //constructor không tham số
@AllArgsConstructor // constructor có tham số
@Builder// bắt buộc phải có AllArgsConstuctor ( builder có thể dùng tham số tùy chọn mà không phải tạo constructor tương ứng)
@FieldDefaults(level = AccessLevel.PRIVATE) // thay cho Access Modifier - private,(tùy theo level) ;

public class ApiResponse <T>{
     int code=49;
     String message;
     T result;


}
