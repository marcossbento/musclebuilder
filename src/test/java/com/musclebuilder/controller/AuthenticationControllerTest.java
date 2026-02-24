package com.musclebuilder.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.musclebuilder.dto.LoginRequest;
import com.musclebuilder.model.RefreshToken;
import com.musclebuilder.service.security.JwtService;
import com.musclebuilder.service.security.RefreshTokenService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AuthenticationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private RefreshTokenService refreshTokenService;

    @MockitoBean
    private AuthenticationManager authenticationManager;

    @MockitoBean
    private UserDetailsService userDetailsService;

    @Test
    void quandoLoginComCredenciaisValidas_deveRetornarStatusOk() throws Exception {

        //ARRANGE
        LoginRequest loginRequest = new LoginRequest("teste@email.com", "senhaTeste");

        UserDetails fakeUserDetails = new User(loginRequest.email(), loginRequest.password(), Collections.emptyList());

        when(authenticationManager.authenticate(any())).thenReturn(null);

        when(userDetailsService.loadUserByUsername(any())).thenReturn(fakeUserDetails);

        when(jwtService.generateToken(any())).thenReturn("fake-jwt-token");

        RefreshToken fakeRefreshToken = new RefreshToken();
        fakeRefreshToken.setToken("fake-refresh-token");
        when(refreshTokenService.createRefreshToken(anyString())).thenReturn(fakeRefreshToken);

        // ACT & ASSERT
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("fake-jwt-token"))
                .andExpect(jsonPath("$.refreshToken").value("fake-refresh-token"));
    }

    @Test
    void quandoLoginComCredenciaisInvalidas_deveRetornarStatusUnauthorized() throws Exception {

        //ARRANGE
        LoginRequest loginRequest = new LoginRequest("teste@email.com", "senhaIncorreta");

        //Desta vez o mock simula uma falha de login, quando authenticate é chamado, lança uma exception de credenciais inválidas
        when(authenticationManager.authenticate(any())).thenThrow(new BadCredentialsException("Credenciais inválidas"));

        // ACT & ASSERT
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized());
    }

}
