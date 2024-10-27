package com.vn.sbit.idenfity_service.dto.request;

import com.vn.sbit.idenfity_service.validator.DobConstraint;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.Set;

@Data//lombok -Thay cho getter , setter , tostring.....
@NoArgsConstructor //constructor không tham số
@AllArgsConstructor // constructor có tham số
@Builder // bắt buộc phải có all constuctor ( builder có thể dùng tham số tùy chọn mà không phải tạo constructor tương ứng)
@FieldDefaults(level = AccessLevel.PRIVATE) // thay cho Access Modifier - private,(tùy theo level) ;

public class UserCreationRequest {
    @Size(min=6,message="USER_USERNAME_INVALID") //message đưa vào name của error, phải đúng tên enum
    @NotNull(message = "Username can not null")
     String userName;

    //spring-sta-validation
    @Size(min = 9,message = "USER_PASSWORD_INVALID")
    @NotNull(message = "Password can not null")
     String passWord;

     String firstName;

     String lastName;

     @DobConstraint(min =16,message = "INVALID_DOB")
     LocalDate dob;

     Set<String> roles;

}
