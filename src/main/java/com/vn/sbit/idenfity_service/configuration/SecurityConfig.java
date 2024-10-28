package com.vn.sbit.idenfity_service.configuration;

import com.vn.sbit.idenfity_service.EnumRoles.Role;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.CorsConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.header.writers.StaticHeadersWriter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;

import java.util.List;


@Configuration //cau hinh su dung bean
@EnableWebSecurity // phan quyen bang request
@EnableMethodSecurity//phan quyen bang method
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SecurityConfig {
    static String [] PUBLIC_ENDPOINT={"/users",
            "/auth/**",
            "/auth/token"//create token
             // authentication token
            //để public vì nếu đã có token thì có thể truy cập vào hệ thống rồi
            // làm mới token trước khi hết hạn

    };
    @Autowired
    CustomJwtDecoder customJwtDecoder;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.authorizeHttpRequests(request ->//ủy quyền
                request.requestMatchers(HttpMethod.POST,PUBLIC_ENDPOINT).permitAll() // cho phép tất cả
//                        .requestMatchers(HttpMethod.GET,"/users/myInfo").hasAnyRole(Role.ADMIN.name(),Role.MANAGER.name(),Role.USER.name())
//                        .requestMatchers(HttpMethod.GET,"/users").hasAnyAuthority("ROLE_"+Role.ADMIN.name(),"ROLE_"+Role.MANAGER.name())

                        .anyRequest().authenticated()//tất cả yêu cầu phải xác thực -> login rồi mới request

        );
        //login bằng token
        httpSecurity.oauth2ResourceServer(oath2 ->
                oath2.jwt(jwtConfigurer -> jwtConfigurer
                        .decoder(customJwtDecoder)// sẽ lấy header và payload ở request rồi matcher với signature(chữ ký) của token user. nếu = chữ ký ban đầu của request thì sẽ match thành công
                        .jwtAuthenticationConverter(jwtAuthenticationConverter())  // hasAuthority
                )       //Exception
                        .authenticationEntryPoint(new JwtAuthenticationEntryPoint()) //401 JWTAuthenticationEntryPoint class
                        .accessDeniedHandler(new CustomAccessDeniedHandler()) //403 CustomAccessDenied
        );

        // csrf tự động bảo mật bởi filter 1 - chặn những request không hợp lệ - để co the create new user -> muốn truy cập phải bỏ chặn
        httpSecurity.csrf(AbstractHttpConfigurer::disable);
//        httpSecurity.cors(Customizer.withDefaults());
        return httpSecurity.build();
    }

    //config cho frontend
    @Bean
    public UrlBasedCorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.setAllowedOrigins(List.of("http://localhost:3000"));
        corsConfiguration.setAllowedMethods(List.of("*")); // Chỉ định các phương thức
        corsConfiguration.setAllowedHeaders(List.of("*"));
        corsConfiguration.setExposedHeaders(List.of("*"));
        corsConfiguration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfiguration);
        return source;
    }
//
    @Bean // dùng cho hasAuthority - converter tu SCOPE to ""
    JwtAuthenticationConverter jwtAuthenticationConverter(){
        JwtGrantedAuthoritiesConverter grantedAuthoritiesConverter= new JwtGrantedAuthoritiesConverter();//grantedConverter
        grantedAuthoritiesConverter.setAuthorityPrefix(""); // default - SCOPE_
        JwtAuthenticationConverter authenticationConverter=new JwtAuthenticationConverter();//authenticationConverter
        authenticationConverter.setJwtGrantedAuthoritiesConverter(grantedAuthoritiesConverter);
        return authenticationConverter;
    }
    //salt :10 -bcrypt
    @Bean
    PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder(10);
    }

}