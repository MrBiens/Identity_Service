package com.vn.sbit.idenfity_service.controller;

import com.vn.sbit.idenfity_service.dto.ApiResponse;
import com.vn.sbit.idenfity_service.dto.request.UserCreationRequest;
import com.vn.sbit.idenfity_service.dto.request.UserUpdateRequest;
import com.vn.sbit.idenfity_service.dto.response.UserResponse;
import com.vn.sbit.idenfity_service.exception.ErrorCode;
import com.vn.sbit.idenfity_service.service.UserService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController //RestAPI - GET-POST-PUT-DELETE
@RequestMapping("/users")
@RequiredArgsConstructor//sẽ tự động Injection dependency mà không ần @Autowired
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true) //những attribute nào không khai báo sẽ mặc định là private, final NameAttribute;
@Slf4j // dùng log để in ra application log
public class UserController {

    //@RequiredArgsConstructor//sẽ tự động Injection dependency mà không ần @Autowired
   UserService userService;

    @PostMapping
    //validation
    public ApiResponse<UserResponse> createUser(@RequestBody @Valid UserCreationRequest request){
        log.info("Controller : create request");
        return ApiResponse.<UserResponse>builder()
                .code(200)
                .result(userService.createUser(request))
                .build();
    }
    @GetMapping
    ApiResponse<List<UserResponse>> getUsers() throws Exception {
        var authentication=SecurityContextHolder.getContext().getAuthentication();//lấy tất cả thông tin của tài khoản đang đăng nhập
        log.info("Username:{}" ,authentication.getName());
//        log.info("Username:{},Authorities:{}" ,authentication.getName(),authentication.getAuthorities()); - in ra nhiều thông tin

        authentication.getAuthorities().forEach(grantedAuthority  ->log.info("Authorization: {}",grantedAuthority.getAuthority()));
        return ApiResponse.<List<UserResponse>>builder()
                .result(userService.getUsers())
                .build();
    }

    @GetMapping("/{userId}")
    public ApiResponse<UserResponse> getUser(@PathVariable("userId") String userId){
        var authentication=SecurityContextHolder.getContext().getAuthentication();//lấy tất cả thông tin của tài khoản đang đăng nhập
        log.info("Username:{},Authorities:{}" ,authentication.getName(),authentication.getAuthorities()); //- in ra nhiều thông tin

        return ApiResponse.<UserResponse>builder()
                .result(userService.getUser(userId))
                .build();
    }
    //info user now
    @GetMapping("/myInfo")
    public ApiResponse<UserResponse> getMyUser(){
        return ApiResponse.<UserResponse>builder()
                .result(userService.getByUserName())
                .build();
    }

    @PutMapping("/{userId}")
    public ApiResponse<UserResponse> updateUser(@PathVariable("userId") String userId,@RequestBody UserUpdateRequest request){
        return ApiResponse.<UserResponse>builder()
                .result(userService.updateUser(userId, request))
                .build();
    }
    @DeleteMapping("/{userId}")
    public ApiResponse<String> deleteUser(@PathVariable("userId") String userId){
         userService.deleteUser(userId);
         return ApiResponse.<String>builder()
                 .result("User has been deleted")
                 .build();

    }

}
