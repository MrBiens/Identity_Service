package com.vn.sbit.idenfity_service.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.vn.sbit.idenfity_service.dto.request.UserCreationRequest;
import com.vn.sbit.idenfity_service.dto.response.UserResponse;
import com.vn.sbit.idenfity_service.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDate;
import java.util.Collections;

@SpringBootTest
@Slf4j
@AutoConfigureMockMvc
@TestPropertySource("/test.properties")
public class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private UserService userService;

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

    //test usercontroller ->
    @Test
    void createUser_validatedRequest_success() throws Exception {
        //Given - initData -request-response
        ObjectMapper objectMapper= new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());//convert localdate to json
        String content=objectMapper.writeValueAsString(userCreationRequest);
        Mockito.when(userService.createUser(ArgumentMatchers.any())).thenReturn(userResponse);
        //When
        mockMvc.perform(MockMvcRequestBuilders
                .post("/users")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(content)
        ).//Then
                andExpect(MockMvcResultMatchers.status().isOk())
         .andExpect(MockMvcResultMatchers.jsonPath("code").value(200))
        ; //http code 200 - ok
    }

    @Test
    @WithMockUser(username ="person9",roles = {})
    void getUserByUserName_validatedRequest_success() throws Exception {
        Mockito.when(userService.getByUserName()).thenReturn(userResponse);

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/myInfo")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("code").value(200));

    }




}
