package com.vn.sbit.idenfity_service.mapper;

import com.vn.sbit.idenfity_service.dto.request.PermissionRequest;
import com.vn.sbit.idenfity_service.dto.request.RoleRequest;
import com.vn.sbit.idenfity_service.dto.response.PermissionResponse;
import com.vn.sbit.idenfity_service.dto.response.RoleResponse;
import com.vn.sbit.idenfity_service.entity.Permission;
import com.vn.sbit.idenfity_service.entity.Role;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RoleMapper {
    @Mapping(target = "permissions",ignore = true) //role in entity - bỏ qua không map
    Role toRole(RoleRequest request);
    RoleResponse toRoleResponse(Role role);


}
