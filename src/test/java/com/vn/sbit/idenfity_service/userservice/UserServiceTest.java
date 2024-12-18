package com.vn.sbit.idenfity_service.userservice;

import com.vn.sbit.idenfity_service.dto.request.UserCreationRequest;
import com.vn.sbit.idenfity_service.dto.request.UserUpdateRequest;
import com.vn.sbit.idenfity_service.dto.response.UserResponse;
import com.vn.sbit.idenfity_service.entity.Permission;
import com.vn.sbit.idenfity_service.entity.Role;
import com.vn.sbit.idenfity_service.entity.User;
import com.vn.sbit.idenfity_service.exception.AppException;
import com.vn.sbit.idenfity_service.exception.ErrorCode;
import com.vn.sbit.idenfity_service.mapper.UserMapper;
import com.vn.sbit.idenfity_service.repository.RoleRepository;
import com.vn.sbit.idenfity_service.repository.UserRepository;
import com.vn.sbit.idenfity_service.service.UserService;
import lombok.With;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.MatcherAssert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;


import java.time.LocalDate;
import java.util.*;


import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;


@SpringBootTest
@TestPropertySource("/test.properties")
public class UserServiceTest {

    private static final Logger log = LoggerFactory.getLogger(UserServiceTest.class);
    @Autowired
    private UserService userService;

    @Autowired
    private RoleRepository roleRepository;

    @MockBean
    private UserRepository userRepository;

    @Mock
    PasswordEncoder passwordEncoder;

    @Mock
    UserMapper userMapper;


    private UserCreationRequest userCreationRequest;
    private UserResponse userResponse;
    private User user;
    private LocalDate dob;
    private Permission permission(){
        return new Permission(
                "PERMISSION_UPDATE",
                "Update"

        );
    }
    private Role role(){
        return new Role(
                "USER",
                "ROLE USER",
                Set.of(permission())
        );
    }







    @BeforeEach
    public void initData(){
        dob=LocalDate.of(1999,12,12);
        userCreationRequest= UserCreationRequest
                .builder()
                .userName("hihihaha3")
                .passWord("123456789")
                .firstName("Tran Song")
                .lastName("Bien")
                .dob(dob)
                .build();



        userResponse=UserResponse
                .builder()
                .id("nihaoma")
                .userName("hihihaha3")
                .firstName("Tran Van")
                .lastName("Bien")
                .dob(dob)
                .build();

        user=User.builder()
                .id("nihaoma")
                .userName("hihihaha3")
                .passWord("123456789")
                .firstName("Tran Song")
                .lastName("Bien")
                .dob(dob)
                .roles(Set.of(role()))
                .build();

    }
    //test method user service create user
    @Test
    void createUser_validRequest_success() {
        // GIVEN
//        when(userRepository.existsByUserName(anyString())).thenReturn(false);
        when(userRepository.save(any())).thenReturn(user);

        /* WHEN */
        var response = userService.createUser(userCreationRequest);
        // THEN

        Assertions.assertThat(response.getId()).isEqualTo("nihaoma");
        Assertions.assertThat(response.getUserName()).isEqualTo("hihihaha3");
    }

    //test ngoại lệ if - throws-  lỗi cụ thể
//    @Test
//    void createUser_userExists_false(){
//        /* Given ( giả sử ) - truyền vào các trường hợp*/
//        when(userRepository.save(user)).thenThrow(new DataIntegrityViolationException(""));
//        /*when(khi nào)
//         lỗi của ngoại lệ */
//        var exception=org.junit.jupiter.api.Assertions.assertThrows(AppException.class,() -> userService.createUser(userCreationRequest));
//        //then(sau đó)
//        Assertions.assertThat(exception.getErrorCode().getCode()).isEqualTo(1001);
//    }

    @Test
    @WithMockUser(username = "hihihaha5",roles = {"ADMIN"}) // tài khoản giả định
    void getByUserName_valid_success(){
        //Tạo một đối tượng Optional chứa giá trị user nếu user không phải là null. Nếu user là null, nó trả về một Optional.empty.
        when(userRepository.findByUserName(anyString())).thenReturn(Optional.ofNullable(user));

        var response =userService.getByUserName();

        Assertions.assertThat(response.getUserName()).isEqualTo("hihihaha3");
        Assertions.assertThat(response.getId()).isEqualTo("nihaoma");

    }
    @Test
    @WithMockUser(username = "hihihaha5",roles = {"ADMIN"}) // tài khoản giả định
    void getByUserName_NotFound_error(){
        //kiểm tra user null
        when(userRepository.findByUserName(anyString())).thenReturn(Optional.empty());

        AppException exception=org.junit.jupiter.api.Assertions.assertThrows(AppException.class,() -> userService.getByUserName());
//        org.junit.jupiter.api.Assertions.assertThrows(AppException.class,() -> userService.getByUserName());
        Assertions.assertThat(exception.getErrorCode().getCode()).isEqualTo(1004);
    }
    @Test
    @WithMockUser(username = "hihihaha5", authorities = {"PERMISSION_UPDATE"})
    void test_UpdateUser_success_Simple() {
        //given ( input-output) + when - giả sử xảy ra
        UserUpdateRequest updateRequest= UserUpdateRequest
                .builder()
                .passWord("12345678910")
                .firstName("Tran Van")
                .lastName("Bien")
                .dob(dob)
                .build();

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(userMapper.toUserResponse(user)).thenReturn(userResponse);
        // when - hành động muốn kiểm tra
        user = userRepository.findById(user.getId()).orElseThrow();
        user.setPassWord(updateRequest.getPassWord());
        user.setFirstName(updateRequest.getFirstName());
        user.setLastName(updateRequest.getLastName());

        userRepository.save(user);
        userResponse= userMapper.toUserResponse(user);
        //userMapper không dùng được cho nên tự build so sánh ( mà không cần thay đổi ở BeforeEach)
                UserResponse userUpdateResponse= UserResponse.builder()
                .id(user.getId())
                .userName(user.getUserName())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .dob(user.getDob())
                .build();
        // then - kiểm tra
        Assertions.assertThat(userResponse.getFirstName()).isEqualTo(updateRequest.getFirstName());
        Assertions.assertThat(userResponse.getLastName()).isEqualTo(updateRequest.getLastName());
        Assertions.assertThat(userResponse.getDob()).isEqualTo(updateRequest.getDob());
        Assertions.assertThat(userResponse.getUserName()).isEqualTo(user.getUserName());  // Kiểm tra UserName vẫn không thay đổi
        Assertions.assertThat(userResponse.getId()).isEqualTo(user.getId());  // Kiểm tra ID vẫn không thay đổi

        /*
        * Bởi vì khi update user sẽ phải truyền vào userId của nó ở Path url mà userId chính là ở User.getId cho nên ta ngầm thừa nhận và lấy ra userId ở phần test.
        Sau đó ta sẽ tìm user dựa trên userId đó. và nó có tên là updateUser
        tiếp theo ta sẽ lấy từ updateRequest các giá trị thay đổi và gán nó vào updateUser đã tìm thấy dựa trên Id.Rồi ta sẽ lưu nó lại,
        Tiếp theo ta sẽ so sánh user sau khi lưu có giống với giá trị updateRequest hay không bằng Assertions.that
         */

    }



    /*
        @Test
        void createUser_RoleNotFound_false(){
          Mockito.when(roleRepository.findAllById(any())).thenReturn(Collections.emptyList());
           var exception= org.junit.jupiter.api.Assertions.assertThrows(AppException.class, () -> userService.createUser(userCreationRequest));
            Assertions.assertThat(exception.getErrorCode().getCode()).isEqualTo(2000);
        }
    */


}
