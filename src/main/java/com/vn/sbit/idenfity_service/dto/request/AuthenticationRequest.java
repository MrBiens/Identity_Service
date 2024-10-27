package com.vn.sbit.idenfity_service.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data//lombok -Thay cho getter , setter , tostring.....
@NoArgsConstructor //constructor không tham số
@AllArgsConstructor // constructor có tham số
@Builder
// bắt buộc phải có all constuctor ( builder có thể dùng tham số tùy chọn mà không phải tạo constructor tương ứng)
@FieldDefaults(level = AccessLevel.PRIVATE) // thay cho Access Modifier - private,(tùy theo level) ;
public class AuthenticationRequest {
    String userName;

    String passWord;
}
