package com.vn.sbit.idenfity_service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.vn.sbit.idenfity_service.dto.request.UserCreationRequest;
import com.vn.sbit.idenfity_service.dto.response.UserResponse;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDate;
import java.util.Collections;

@SpringBootTest
@Slf4j
@AutoConfigureMockMvc
@Testcontainers
// connection to mysql - not test properties(h2)
public class UserControllerIntegrationTest {
    @Container
    private static final MySQLContainer<?> MY_SQL_CONTAINER = new MySQLContainer<>("mysql:latest");

    //sẽ tự động tạo 1 container với config này và run test , không dùng tới db gốc
    @DynamicPropertySource
    private static void configureDatasource(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", MY_SQL_CONTAINER::getJdbcUrl);
        registry.add("spring.datasource.driverClassName",() -> MY_SQL_CONTAINER.getDriverClassName());
        registry.add("spring.datasource.username",()->MY_SQL_CONTAINER.getUsername());
        registry.add("spring.datasource.password",() -> MY_SQL_CONTAINER.getPassword());
        registry.add("spring.jpa.hibernate.ddl-auto",() -> "update" );
    }

    @Autowired
    private MockMvc mockMvc;


    private UserCreationRequest userCreationRequest;
    private UserResponse userResponse;
    private LocalDate dob;

    @BeforeEach
    public void initData(){
        dob=LocalDate.of(1999,12,12);
        userCreationRequest= UserCreationRequest
                .builder()
                .userName("person9")
                .passWord("123456789")
                .firstName("Tran Song")
                .lastName("Bien")
                .dob(dob)
                .roles(Collections.singleton("USER"))
                .build();
        userResponse=UserResponse
                .builder()
                .userName("person9")
                .firstName("Tran Song")
                .lastName("Bien")
                .dob(dob)
                .build();

    }

    // sẽ insert thuc te toi dbms - cho nen input se phai chuan ( khong duoc demo ) <=> output
    @Test
    void createUser_validatedRequest_success() throws Exception {
        //Given - initData -request-response
        ObjectMapper objectMapper= new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());//convert localdate to json
        String content=objectMapper.writeValueAsString(userCreationRequest);
        //When
        var response = mockMvc.perform(MockMvcRequestBuilders
                .post("/users")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(content)
        ).//Then
                andExpect(MockMvcResultMatchers.status().isOk())
         .andExpect(MockMvcResultMatchers.jsonPath("code").value(200)) //http code 200 - ok
        ;
        log.info(" Result:{}",response.andReturn().getResponse().getContentAsString());
    }

    @Test
    void createUser_usernameInvalid_false() throws Exception {
        userCreationRequest.setUserName("bvc");
        ObjectMapper objectMapper= new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());//convert localdate to json
        String content=objectMapper.writeValueAsString(userCreationRequest);

         mockMvc.perform(MockMvcRequestBuilders
                        .post("/users")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(content)
                ).andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("code").value("1002"))
                .andExpect(MockMvcResultMatchers.jsonPath("message").value("Username must be at least 6 character"));
    }


}
