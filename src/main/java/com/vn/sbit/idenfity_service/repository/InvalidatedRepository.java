package com.vn.sbit.idenfity_service.repository;

import com.vn.sbit.idenfity_service.entity.InvalidatedToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InvalidatedRepository extends JpaRepository<InvalidatedToken,String> {
}
