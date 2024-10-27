package com.vn.sbit.idenfity_service.controller;

import com.vn.sbit.idenfity_service.dto.ApiResponse;
import com.vn.sbit.idenfity_service.dto.request.PermissionRequest;
import com.vn.sbit.idenfity_service.dto.response.PermissionResponse;
import com.vn.sbit.idenfity_service.service.PermissionService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController //RestAPI - GET-POST-PUT-DELETE
@RequestMapping("/permissions")
@RequiredArgsConstructor//sẽ tự động Injection dependency mà không ần @Autowired
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true) //những attribute nào không khai báo sẽ mặc định là private, final NameAttribute;
@Slf4j // dùng log để in ra application log
public class PermissionController {
    PermissionService permissionService;


    @PostMapping
    ApiResponse<PermissionResponse> create(@RequestBody PermissionRequest request){
        return ApiResponse.<PermissionResponse>builder()
                .result(permissionService.create(request))
                .build();
    }
    @GetMapping
    ApiResponse<List<PermissionResponse>> getAll(){
        return ApiResponse.<List<PermissionResponse>>builder()
                .result(permissionService.getAll())
                .build();
    }
    @DeleteMapping("/{permission}")
    ApiResponse<String> delete(@PathVariable String permission){
        permissionService.delete(permission);
        return ApiResponse.<String>builder()
                .result("Permission has been delete")
                .build();
    }


}
