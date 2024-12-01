package com.vn.sbit.idenfity_service.userservice;

import com.vn.sbit.idenfity_service.dto.request.UserCreationRequest;
import com.vn.sbit.idenfity_service.dto.request.UserUpdateRequest;
import com.vn.sbit.idenfity_service.dto.response.UserResponse;
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
    private UserUpdateRequest updateRequest;
    private UserResponse userResponse;
    private User user;
    private LocalDate dob;



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

        updateRequest= UserUpdateRequest
                .builder()
                .passWord("123456789")
                .firstName("Tran Song")
                .lastName("Bien")
                .dob(dob)
                .build();


        userResponse=UserResponse
                .builder()
                .id("nihaoma")
                .userName("hihihaha3")
                .firstName("Tran Song")
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
        when(userRepository.findByUserName(anyString())).thenReturn(Optional.ofNullable(user));

        var response =userService.getByUserName();

        Assertions.assertThat(response.getUserName()).isEqualTo("hihihaha3");
        Assertions.assertThat(response.getId()).isEqualTo("nihaoma");

    }
    @Test
    @WithMockUser(username = "hihihaha5",roles = {"ADMIN"}) // tài khoản giả định
    void getByUserName_NotFound_error(){
        //kiểm tra user null
        when(userRepository.findByUserName(anyString())).thenReturn(Optional.ofNullable(null));

        AppException exception=org.junit.jupiter.api.Assertions.assertThrows(AppException.class,() -> userService.getByUserName());
//        org.junit.jupiter.api.Assertions.assertThrows(AppException.class,() -> userService.getByUserName());

        Assertions.assertThat(exception.getErrorCode().getCode()).isEqualTo(1004);
    }
    @Test
    @WithMockUser(username = "hihihaha5", authorities = {"PERMISSION_UPDATE"})
    void test_UpdateUser_success_Simple() {
        //given - data

        //when
        when(userRepository.findById(user.getId())).thenReturn(Optional.ofNullable(user));
        when(userRepository.save(user)).thenReturn(user);

        //then
        Assertions.assertThat(userResponse.getFirstName()).isEqualTo(user.getFirstName());






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
