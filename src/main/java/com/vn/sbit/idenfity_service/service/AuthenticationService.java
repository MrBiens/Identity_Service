package com.vn.sbit.idenfity_service.service;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.vn.sbit.idenfity_service.dto.request.AuthenticationRequest;
import com.vn.sbit.idenfity_service.dto.request.IntrospectRequest;
import com.vn.sbit.idenfity_service.dto.request.LogOutRequest;
import com.vn.sbit.idenfity_service.dto.request.RefreshTokenRequest;
import com.vn.sbit.idenfity_service.dto.response.AuthenticationResponse;
import com.vn.sbit.idenfity_service.dto.response.IntrospectResponse;
import com.vn.sbit.idenfity_service.entity.InvalidatedToken;
import com.vn.sbit.idenfity_service.entity.User;
import com.vn.sbit.idenfity_service.exception.AppException;
import com.vn.sbit.idenfity_service.exception.ErrorCode;
import com.vn.sbit.idenfity_service.repository.InvalidatedRepository;
import com.vn.sbit.idenfity_service.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.StringJoiner;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j

public class AuthenticationService {
    @NonFinal // đánh dấu để lombok không inject dependency vào construct
    @Value("${jwt.signerKey}")
    protected String SIGNER_KEY;//cipher key-khóa bí mật -generate random

    @NonFinal
    @Value("${jwt.valid-duration}")
    protected long VALID_DURATION; //time expiry

    @NonFinal
    @Value("${jwt.refreshable-duration}")
    protected long REFRESHABLE_DURATION; //time add

    UserRepository userRepository;

    InvalidatedRepository invalidatedRepository;

    // Step 1: User login create Token token ->
    // Step 2: User authentication token to be able to use system -> view verifier token (1. expiryTime 2.LogOut?)
    // Step 3: Log out -> Invalidated save ID Token  -> transfer ID token to Repository.save
    // -> if token logout cannot use system


    //Kiểm tra token có hợp lệ hay không
    public IntrospectResponse introspectResponse(IntrospectRequest request) throws JOSEException, ParseException {
        var token = request.getToken();
        boolean isValid = true; //check token
        //method verifier
        try {
            verifier(token, false); //nếu tìm thấy verifier tìm kiếm ở id Invalidated nếu có sẽ error - vì đã log out hoặc token đã hết hạn
        } catch (AppException e) {
            isValid = false;  // -> vì condition ở trên nên trả về false
        }
        return IntrospectResponse.builder()
                .valid(isValid) // token vẫn chưa logout và còn hiệu lực
                .build();
    }


    //Xác thực để trả về token
    public AuthenticationResponse Authenticate(AuthenticationRequest request) {
        log.info("JWT_SIGNERKEY:{}",SIGNER_KEY);
        var user = userRepository.findByUserName(request.getUserName()).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
        //xác thực mật khẩu có khớp với mật khẩu đã lưu
        boolean authenticated = passwordEncoder.matches(request.getPassWord(), user.getPassWord());
        if (!authenticated) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }
        var token = generateToken(user);
        return AuthenticationResponse.builder()
                .token(token)
                .authenticated(true)
                .build();
    }

    // hủy bỏ token
    public void LogOut(LogOutRequest request) throws ParseException, JOSEException {
        var signToken = verifier(request.getToken(), true); // không quan trọng là có + thêm time hay không vì token sẽ bị log out

        String jit = signToken.getJWTClaimsSet().getJWTID(); //uuid
        Date expiryTime = signToken.getJWTClaimsSet().getExpirationTime();

        InvalidatedToken invalidatedToken = InvalidatedToken.builder()
                .id(jit)
                .expiryTime(expiryTime)
                .build();
        invalidatedRepository.save(invalidatedToken);
    }

    //refresh token before expiryTime
    public AuthenticationResponse RefreshToken(RefreshTokenRequest request) throws ParseException, JOSEException {
        SignedJWT signedJWT = verifier(request.getToken(), true);   //mục đích là tăng thêm time để Refresh token nếu token hết hạn

        String jit = signedJWT.getJWTClaimsSet().getJWTID();
        Date expiryTime = signedJWT.getJWTClaimsSet().getExpirationTime();

        //transfer token cu vao invalidated table -> khong dung nua
        InvalidatedToken invalidatedToken = InvalidatedToken
                .builder()
                .id(jit)
                .expiryTime(expiryTime)
                .build();
        invalidatedRepository.save(invalidatedToken);

        String userName = signedJWT.getJWTClaimsSet().getSubject();

        User user = userRepository.findByUserName(userName).orElseThrow(
                () -> new AppException(ErrorCode.UNAUTHENTICATED)
        );
        String newToken = generateToken(user);

        return AuthenticationResponse
                .builder()
                .token(newToken)
                .authenticated(true)
                .build();
    }

    /*Verifier (isRefresh) true ?sẽ lấy thời gian gốc tạo token và + thêm thời gian có thể refresh token 1 lần nữa false: sẽ lấy thời gian expire cũ của token
     *  -> Nếu thời gian quá dài thì sẽ không thể refresh từ token cũ nữa. Mục đích là giới hạn quyền truy cập - đề phòng hacker có được token -> Bảo mật
     *   Thời gian hoạt động của token mới sẽ giống với token cũ -> VD 1h sẽ hết hạn
     * */


    //xác thực token
    public SignedJWT verifier(String token, boolean isRefresh) throws ParseException, JOSEException {
        SignedJWT signedJWT = SignedJWT.parse(token);//Phân tích cú pháp chuỗi token thành một đối tượng SignedJWT.

        JWSVerifier verifier = new MACVerifier(SIGNER_KEY.getBytes());// Private key sử dụng để kết hợp tạo ra token mới.

        Date expiryTime = (isRefresh)
                // lấy thời gian tạo token + thời gian hết hạn , chuyển về mili giây rồi chuyển vào đối tượng ngày mới .Mục đích là tăng thêm time để Refresh token nếu token hết hạn
            ? (new Date(signedJWT
                .getJWTClaimsSet()
                .getIssueTime()
                .toInstant()
                .plus(REFRESHABLE_DURATION, ChronoUnit.HOURS)
                .toEpochMilli()))
            : signedJWT.getJWTClaimsSet().getExpirationTime();//Lấy thời gian hết hạn của token từ claims set của JWT.

        // sẽ lấy token hiện tại và tạo 1 bản sao rồi truyền vào primarykey đã được mã hóa rồi so sánh với token cũ
        var verified = signedJWT.verify(verifier);//trả về true nếu token signature giống với signature được tạo để kiển tra
        //Nếu xác thực thất bại và thời gian hết hạn trước thời gian hiện tại -> tức là sai - !(success- true) -> false
        if (!(verified && expiryTime.after(new Date()))) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }
        // kiem tra neu token da logout
        if (invalidatedRepository.existsById(signedJWT.getJWTClaimsSet().getJWTID())) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }
        return signedJWT;

    }

    //Lấy token từ thông tin đã đưa vào
    String generateToken(User user) {
        //dependency nimbus Token gồm [header;payload;signature(header,payload)]
        JWSHeader jwsHeader = new JWSHeader(JWSAlgorithm.HS512); // thuat toan SHA512

        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .subject(user.getUserName())      //username
                .issuer("com.vn.sbit") //domain
                .issueTime(new Date())  //time create
                .expirationTime(
                        new Date(Instant.now().plus(VALID_DURATION, ChronoUnit.HOURS).toEpochMilli())
                )// expiry time
                .jwtID(UUID.randomUUID().toString()) //random uuid 32char
                .claim("scope", buildScope(user))
                .build();
        Payload payload = new Payload(jwtClaimsSet.toJSONObject());

        JWSObject jwsObject = new JWSObject(jwsHeader, payload);//jws= header + payload

        try {
            // state =header+payload  + SIGNED(ENUM SIGNED)  - state = (base64UrlEncode(header) + "." +  base64UrlEncode(payload))
            // signature = jwsObject.sign= signWithHMAC(state+primary key,secretKey )
            jwsObject.sign(new MACSigner(SIGNER_KEY.getBytes()));
            return jwsObject.serialize();//return object jwt đc ký thành dạng String bằng serialize(false)-cụ thể là jwsObject = header,paypal+ signature
        } catch (JOSEException e) {
            throw new RuntimeException(e);
        }
    }

    //ROLE USER -TOKEN(JWT)
    String buildScope(User user) { //Get user.role and role.permission //Example: "ADMIN  - CREATE UPDATE DELETE"
        StringJoiner stringJoiner = new StringJoiner(" "); // joiner: [""  ,"" ,""]
        if (!CollectionUtils.isEmpty(user.getRoles())) { //IF USER.ROLE NOT NULL
            user.getRoles().forEach(role
                    -> {
                stringJoiner.add("ROLE_" + role.getName());  // AUTO ADD ROLE_ VÀO TOKEN (TỪ ADMIN -> ROLE_ADMIN)
                if (!CollectionUtils.isEmpty(role.getPermissions())) { //IF ROLE.PERMISSION NOT NULL
                    role.getPermissions().forEach(permission
                            -> {
                        stringJoiner.add("PERMISSION_" + permission.getName());// AUTO ADD PERMISSION VÀO TOKEN (TỪ CREATE -> PERMISSION_CREATE)
                    });
                }

            });
        }
        return stringJoiner.toString();
    }


}
