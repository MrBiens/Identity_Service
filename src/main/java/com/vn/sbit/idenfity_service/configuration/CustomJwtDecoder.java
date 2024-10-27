package com.vn.sbit.idenfity_service.configuration;

import com.nimbusds.jose.JOSEException;
import com.vn.sbit.idenfity_service.dto.request.IntrospectRequest;
import com.vn.sbit.idenfity_service.service.AuthenticationService;
import lombok.Data;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.text.ParseException;
import java.util.Objects;

@Component //cả 1 class là bean
@Data
public class CustomJwtDecoder implements JwtDecoder {
    @Value("${jwt.signerKey}")//springframework.annotation.Value; // dùng để injection 1 property ở application vào variable
    private String signerKey ;

    @Autowired
    private AuthenticationService authenticationService;

    private NimbusJwtDecoder nimbusJwtDecoder=null;

    @Override
    public Jwt decode(String token) throws JwtException {
        //check xem token co chuan khong
        try {
             var response=authenticationService.introspectResponse(IntrospectRequest
                    .builder()
                    .token(token)
                    .build()
            );
             if (!response.isValid()) throw new JwtException("Token invalid");
        } catch (JOSEException | ParseException e) { //or bitwise
            throw new RuntimeException(e);
        }

        //chua het han thi van se thuc hien binh thuong
        //dùng để lấy private key và mã hóa nó theo algorithm hs512 để tạo ra chữ ký token jwt nhằm xác minh token của user nhập vào với token hệ thống
        if(Objects.isNull(nimbusJwtDecoder)) {
            SecretKeySpec secretKeySpec = new SecretKeySpec(signerKey.getBytes(), "HS512");
            nimbusJwtDecoder=NimbusJwtDecoder
                    .withSecretKey(secretKeySpec)
                    .macAlgorithm(MacAlgorithm.HS512)
                    .build();
        }

        System.out.println(nimbusJwtDecoder);
        // decode thành 3 phần header payload và signature bằng primary key + algorithm
        return nimbusJwtDecoder.decode(token);
    }

}
