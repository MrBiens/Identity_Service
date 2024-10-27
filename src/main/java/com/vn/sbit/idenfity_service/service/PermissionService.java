package com.vn.sbit.idenfity_service.service;


import com.vn.sbit.idenfity_service.dto.request.PermissionRequest;
import com.vn.sbit.idenfity_service.dto.response.PermissionResponse;
import com.vn.sbit.idenfity_service.entity.Permission;
import com.vn.sbit.idenfity_service.mapper.PermissionMapper;
import com.vn.sbit.idenfity_service.repository.PermissionRepository;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor //autowired
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
@Slf4j
public class PermissionService {
    PermissionRepository permissionRepository;
    PermissionMapper permissionMapper;

    public PermissionResponse create(PermissionRequest request){
            Permission permission = permissionMapper.toPermission(request);
            permission=permissionRepository.save(permission);
            return permissionMapper.toPermissionResponse(permission);
    }


    public List<PermissionResponse> getAll(){
        var permission=permissionRepository.findAll();
        //        permission.stream().map(permission1 -> permissionMapper.toPermissionRespone(permission1)).toList();
        return permission.stream().map(permissionMapper::toPermissionResponse).toList();
    }

    public void delete (String permission){
        permissionRepository.deleteById(permission);
    }
}
