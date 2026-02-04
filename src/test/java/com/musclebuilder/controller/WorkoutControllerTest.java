package com.musclebuilder.controller;

import com.musclebuilder.dto.WorkoutCreateDTO;
import com.musclebuilder.dto.WorkoutExerciseCreateDTO;
import com.musclebuilder.dto.WorkoutResponseDTO;
import com.musclebuilder.service.WorkoutService;
import com.musclebuilder.service.security.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(WorkoutController.class)
@AutoConfigureMockMvc(addFilters = false)
public class WorkoutControllerTest {

    @Autowired
    private MockMvc mockMvc; // Simula reqs HTTP

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private WorkoutService workoutService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UserDetailsService userDetailsService;

    @Test
    void criarTreino_ComDadosValidos_DeveRetornar201() throws Exception {
        // ARRANGE
        WorkoutExerciseCreateDTO fakeExercise =  new WorkoutExerciseCreateDTO(1L, 3, 10, 60.0, null);

        WorkoutCreateDTO inputDto = new WorkoutCreateDTO("Treino A", "Descrição", null, List.of(fakeExercise));
        WorkoutResponseDTO outputDto = new WorkoutResponseDTO(1L, "Treino A", "Descrição", null, 1L, null, null, null, null, null, List.of(), null, null);

        when(workoutService.createWorkout(any())).thenReturn(outputDto);

        //ACT e ASSERT
        mockMvc.perform(post("/api/workouts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(inputDto))) // Envia JSON
                .andExpect(status().isCreated()) // Espera HTTP 201
                .andExpect(jsonPath("$.id").value(1L)) // Verifica o ID no JSON
                .andExpect(jsonPath("$.name").value("Treino A"));
    }

    @Test
    void criarTreino_SemNome_DeveRetornar400() throws Exception {
        // ARRANGE - DTO inválido com nome vazio
        WorkoutCreateDTO invalidDto = new WorkoutCreateDTO("", "Desc",  null, List.of());

        // ACT & ASSERT
        mockMvc.perform(post("/api/workouts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest()); // @Valid deve barrar isso
    }
}
