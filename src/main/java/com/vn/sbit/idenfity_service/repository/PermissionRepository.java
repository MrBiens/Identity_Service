package com.vn.sbit.idenfity_service.repository;

import com.vn.sbit.idenfity_service.entity.Permission;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository

public interface PermissionRepository extends JpaRepository<Permission,String > {
}
