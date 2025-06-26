package com.musclebuilder.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.musclebuilder.dto.LoginRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
public class AuthenticationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    //INjeta o ObjectMapper para converter objetos Java para JSON
    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AuthenticationManager authenticationManager;

    @Test
    void quandoLoginComCredenciaisValidas_deveRetornarStatusOk() throws Exception {

        //ARRANGE
        LoginRequest loginRequest = new LoginRequest("teste@email.com", "senhaTeste");

        when(authenticationManager.authenticate(any())).thenReturn(null);

        // ACT & ASSERT
        mockMvc.perform(post("/api/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk());
    }

    @Test
    void quandoLoginComCredenciaisInvalidas_deveRetornarStatusUnauthorized() throws Exception {

        //ARRANGE
        LoginRequest loginRequest = new LoginRequest("teste@email.com", "senhaIncorreta");

        //Desta vez o mock simula uma falha de login, quando authenticate é chamado, lança uma exception de credenciais inválidas
        when(authenticationManager.authenticate(any())).thenThrow(new BadCredentialsException("Credenciais inválidas"));

        // ACT & ASSERT
        mockMvc.perform(post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized());
    }

}
