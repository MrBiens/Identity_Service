package com.vn.sbit.idenfity_service.mapper;

import com.vn.sbit.idenfity_service.dto.request.PermissionRequest;
import com.vn.sbit.idenfity_service.dto.response.PermissionResponse;
import com.vn.sbit.idenfity_service.entity.Permission;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PermissionMapper {
    Permission toPermission(PermissionRequest request);
    PermissionResponse toPermissionResponse(Permission request);


}
