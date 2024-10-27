package com.vn.sbit.idenfity_service.service;


import com.vn.sbit.idenfity_service.dto.request.PermissionRequest;
import com.vn.sbit.idenfity_service.dto.request.RoleRequest;
import com.vn.sbit.idenfity_service.dto.response.PermissionResponse;
import com.vn.sbit.idenfity_service.dto.response.RoleResponse;
import com.vn.sbit.idenfity_service.entity.Permission;
import com.vn.sbit.idenfity_service.entity.Role;
import com.vn.sbit.idenfity_service.mapper.PermissionMapper;
import com.vn.sbit.idenfity_service.mapper.RoleMapper;
import com.vn.sbit.idenfity_service.repository.PermissionRepository;
import com.vn.sbit.idenfity_service.repository.RoleRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor //autowired
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
@Slf4j
public class RoleService {
    RoleRepository roleRepository;
    RoleMapper roleMapper;

    PermissionRepository permissionRepository;

    public RoleResponse create(RoleRequest request){
        Role role = roleMapper.toRole(request);

        var permissions = permissionRepository.findAllById(request.getPermissions()); // List<Permission> because findAllById as List
        role.setPermissions(new HashSet<>(permissions)); //vì role<Set>
        role=roleRepository.save(role);
        return roleMapper.toRoleResponse(role);

    }
    public List<RoleResponse> getAll(){
        return roleRepository.findAll()
                .stream()
                .map(roleMapper::toRoleResponse)
                .toList();
    }

    public void delete (String role){
        roleRepository.deleteById(role);
    }
}
