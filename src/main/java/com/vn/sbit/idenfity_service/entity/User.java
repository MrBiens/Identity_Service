package com.vn.sbit.idenfity_service.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor //constructor null
@AllArgsConstructor // constructor full property
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class User {
     @Id
     @GeneratedValue(strategy = GenerationType.UUID)
     String id;

     @Column(name = "username",unique = true,columnDefinition = "VARCHAR(255) COLLATE utf8mb4_unicode_ci") //unique field - không phân biệt hoa thường (utf8mb4_bin là phân biệt)
     String userName;

     String passWord;

     String firstName;

     String lastName;

     LocalDate dob;

     @ManyToMany //1 user có thể nhiều quyền - 1 quyền có thể nhiều user có
     Set<Role> roles;





}
