package com.vn.sbit.idenfity_service.service;

import com.vn.sbit.idenfity_service.dto.request.UserCreationRequest;
import com.vn.sbit.idenfity_service.dto.request.UserUpdateRequest;
import com.vn.sbit.idenfity_service.dto.response.UserResponse;
import com.vn.sbit.idenfity_service.entity.Role;
import com.vn.sbit.idenfity_service.entity.User;
import com.vn.sbit.idenfity_service.exception.AppException;
import com.vn.sbit.idenfity_service.exception.ErrorCode;
import com.vn.sbit.idenfity_service.mapper.UserMapper;
import com.vn.sbit.idenfity_service.repository.RoleRepository;
import com.vn.sbit.idenfity_service.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.util.HashSet;
import java.util.List;

@Service
@RequiredArgsConstructor   //Dependency injection constructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j

//@FielDefaults(level=AccessLevel.PRIVATE, makeFilnal = true) -- những attribute nào không khai báo sẽ mặc định là private final NameAttribute;
public class UserService {
    UserRepository userRepository;

    UserMapper userMapper;

    RoleRepository roleRepository;

    PasswordEncoder passwordEncoder;

    @Transactional
    public UserResponse createUser(UserCreationRequest request) {
        if (userRepository.existsByUserName(request.getUserName())){
            throw new AppException(ErrorCode.USER_EXISTED);
        }
        User user = userMapper.toUser(request);

        user.setPassWord(passwordEncoder.encode(request.getPassWord()));

        HashSet<Role> role = new HashSet<>();
        String roleId = com.vn.sbit.idenfity_service.EnumRoles.Role.USER.toString();

        roleRepository.findById(roleId).ifPresent(yes -> role.add(yes)); // bởi vì lambda method ifPresent sẽ lưu nếu nó != null
        user.setRoles(role); //set role này làm mặc định khi tạo tài khoản

        try {
            user = userRepository.save(user);
        } catch (DataIntegrityViolationException exception) {
            throw new AppException(ErrorCode.USER_EXISTED);
        }
        return userMapper.toUserResponse(userRepository.save(user));
    }

    //get info user login now
//    @PostAuthorize("returnObject.userName == authentication.name or hasRole('ROLE_ADMIN') or hasRole('MANAGER')")
    public UserResponse getByUserName() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        String userName = authentication.getName();
        User user = userRepository.findByUserName(userName).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        return userMapper.toUserResponse(user);
    }

    @PostAuthorize("returnObject.userName == authentication.name or hasRole('ROLE_ADMIN') or hasRole('MANAGER')")
// nếu userName trả về = user đang đang nhập thì return
    public UserResponse getUser(String id) {
        return userMapper.toUserResponse(userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found")
                ));
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public List<UserResponse> getUsers() throws Exception {
        return userRepository.findAll()
                .stream()
                .map(userMapper::toUserResponse)
                .toList();

        //   List<User> users = userRepository.findAll();
        //        List<UserResponse> userResponses = users.stream()
        //                .map(user -> userMapper.toUserResponse(user))
        //                .toList();
        //        return userResponses;
    }


    @PreAuthorize("hasAuthority('PERMISSION_UPDATE')")//thực thi nếu user role có permission...
    public UserResponse updateUser(String userId, UserUpdateRequest request) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        userMapper.updateUser(user, request);
        user.setPassWord(passwordEncoder.encode(request.getPassWord())); // class securityConfig
        var roles = roleRepository.findAllById(request.getRoles());
        user.setRoles(new HashSet<>(roles)); //because user_role property = SET

        return userMapper.toUserResponse(userRepository.save(user));
    }

    @PostAuthorize("hasRole('ADMIN')") // sẽ thực thi nhưng nếu không phải là role admin thì sẽ không return
    public void deleteUser(String userId) {
        userRepository.deleteById(userId);
    }

}
