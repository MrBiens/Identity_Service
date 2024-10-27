package com.vn.sbit.idenfity_service.controller;

import com.vn.sbit.idenfity_service.dto.ApiResponse;
import com.vn.sbit.idenfity_service.dto.request.RoleRequest;
import com.vn.sbit.idenfity_service.dto.response.RoleResponse;
import com.vn.sbit.idenfity_service.service.RoleService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController //RestAPI - GET-POST-PUT-DELETE
@RequestMapping("/roles")
@RequiredArgsConstructor//sẽ tự động Injection dependency mà không ần @Autowired
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true) //những attribute nào không khai báo sẽ mặc định là private, final NameAttribute;
@Slf4j // dùng log để in ra application log
public class RoleController {
    RoleService roleService;

    @PostMapping
    public ApiResponse<RoleResponse> create(@RequestBody RoleRequest request){
        return ApiResponse.<RoleResponse>builder()
                .result(roleService.create(request))
                .build();
    }
    @GetMapping
    public ApiResponse<List<RoleResponse>> getAll(){
        return ApiResponse.<List<RoleResponse>>builder()
                .result(roleService.getAll())
                .build();
    }

    @DeleteMapping("/{role}")
    public ApiResponse<String> delete(@PathVariable String role){
        roleService.delete(role);
       return ApiResponse.<String>builder()
                .result("Role has been delete")
                .build();
    }


}
