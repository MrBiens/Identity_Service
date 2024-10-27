package com.vn.sbit.idenfity_service.dto.request;

import com.vn.sbit.idenfity_service.entity.Permission;
import jakarta.persistence.ManyToMany;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Set;

@Data//lombok -Thay cho getter , setter , tostring.....
@NoArgsConstructor //constructor không tham số
@AllArgsConstructor // constructor có tham số
@Builder //tạo ra 1 static builder trả về
// bắt buộc phải có all constuctor ( builder có thể dùng tham số tùy chọn mà không phải tạo constructor tương ứng và không phải tạo đối tượng để using)
@FieldDefaults(level = AccessLevel.PRIVATE) // thay cho Access Modifier - private,(tùy theo level) ;
public class RoleRequest {
    String name;

    String description;

    Set<String> permissions; // vd chức năng
}
