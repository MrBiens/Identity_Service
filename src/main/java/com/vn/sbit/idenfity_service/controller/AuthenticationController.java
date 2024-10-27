package com.vn.sbit.idenfity_service.controller;

import com.nimbusds.jose.JOSEException;
import com.vn.sbit.idenfity_service.dto.ApiResponse;
import com.vn.sbit.idenfity_service.dto.request.AuthenticationRequest;
import com.vn.sbit.idenfity_service.dto.request.IntrospectRequest;
import com.vn.sbit.idenfity_service.dto.request.LogOutRequest;
import com.vn.sbit.idenfity_service.dto.request.RefreshTokenRequest;
import com.vn.sbit.idenfity_service.dto.response.AuthenticationResponse;
import com.vn.sbit.idenfity_service.dto.response.IntrospectResponse;
import com.vn.sbit.idenfity_service.exception.ErrorCode;
import com.vn.sbit.idenfity_service.service.AuthenticationService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor // tao construct de autowired
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
public class AuthenticationController {
    AuthenticationService authenticationService;

    //create token
    @PostMapping("/token")
    public ApiResponse<AuthenticationResponse> authenticationApiResponse(@RequestBody AuthenticationRequest request){
        var result=authenticationService.Authenticate(request);

        return ApiResponse.<AuthenticationResponse>builder()
                .result(result)
                .build();
    }


    //xac thuc token
    @PostMapping("/introspect")
    public ApiResponse<IntrospectResponse> introspectApiResponse(@RequestBody IntrospectRequest request) throws ParseException, JOSEException {
        var result=authenticationService.introspectResponse(request);
        return ApiResponse.<IntrospectResponse>builder()
                .result(result)
                .build();

    }

    //logout token
    @PostMapping("/logout")
    public ApiResponse<Void> logOutToken(@RequestBody LogOutRequest request) throws ParseException, JOSEException {
         authenticationService.LogOut(request);
         return ApiResponse.<Void>builder()
                 .build();
    }

    //refresh token
    @PostMapping("/refresh")
    public ApiResponse<AuthenticationResponse> refreshToken(@RequestBody RefreshTokenRequest request) throws ParseException, JOSEException {
        var result=authenticationService.RefreshToken(request);
        return ApiResponse.<AuthenticationResponse>builder()
                .result(result)
                .build();
    }




}
