package com.musclebuilder.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.musclebuilder.dto.DashboardDTO;
import com.musclebuilder.dto.StartWorkoutRequest;
import com.musclebuilder.dto.WorkoutLogResponseDTO;
import com.musclebuilder.model.User;
import com.musclebuilder.repository.UserRepository;
import com.musclebuilder.service.GamificationService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class GamificationFlowTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    // Usado para converter objetos Java para JSON
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private GamificationService gamificationService;

    @Test
    @WithUserDetails("jorge@gmail.com")
    void quandoUsuarioCompletaTreino_deveReceberXPCorretamente() throws Exception {
        // --- 1. PREPARAÇÃO (Arrange) ---

        // Buscamos o usuário para saber seu XP inicial
        User userAntes = userRepository.findByEmail("jorge@gmail.com").orElseThrow();
        long xpInicial = userAntes.getExperiencePoints();

        // NOVO: Criamos um DTO para iniciar um novo treino
        StartWorkoutRequest startRequest = new StartWorkoutRequest(1L, "Push/Pull Padrão");

        // NOVO: Simulamos uma requisição POST para INICIAR o treino
        MvcResult startResult = mockMvc.perform(post("/api/workout-logs/start")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(startRequest)))
                .andExpect(status().isCreated()) // Esperamos um 201 Created
                .andReturn(); // Pegamos o resultado da requisição

        // NOVO: Extraímos o ID do treino que acabamos de criar a partir da resposta JSON
        String responseBody = startResult.getResponse().getContentAsString();
        WorkoutLogResponseDTO startedLog = objectMapper.readValue(responseBody, WorkoutLogResponseDTO.class);
        Long workoutLogId = startedLog.id();


        // --- 2. AÇÃO (Act) ---

        // Usamos o ID dinâmico que acabamos de obter para completar o treino
        mockMvc.perform(post("/api/workout-logs/" + workoutLogId + "/complete"))
                .andExpect(status().isOk()); // Agora esperamos um 200 OK

        // --- 3. VERIFICAÇÃO (Assert) ---

        // Buscamos o usuário novamente para ver se o XP mudou.
        User userDepois = userRepository.findByEmail("jorge@gmail.com").orElseThrow();
        long xpFinal = userDepois.getExperiencePoints();

        // A verificação continua a mesma: o XP final deve ser maior que o inicial.
        assertThat(xpFinal).isGreaterThan(xpInicial);

        // Agora podemos até fazer um cálculo exato!
        // O treino "Push/Pull Padrão" não tem volume inicial, então o XP ganho deve ser exatamente 100.
        long xpEsperado = xpInicial + 100; // 100 (fixo) + 0 (volume)
        assertThat(xpFinal).isEqualTo(xpEsperado);
    }

    @Test
    @WithUserDetails("jorge@gmail.com")
    @DisplayName("Quando ganha XP suficiente para múltiplos níveis, a dashboard deve exibir o progresso correto")
    void quandoGanhaMuitoXP_DashboardDeveExibirProgressoCorreto() throws Exception {
        // --- 1. PREPARAÇÃO (Arrange) ---

        // Buscamos o usuário e definimos um estado inicial.
        User user = userRepository.findByEmail("jorge@gmail.com").orElseThrow();
        user.setLevel(1); // Garantimos que ele começa no nível 1
        // Damos a ele 900 XP. Para passar para o nível 2, ele precisa de 1000.
        user.setExperiencePoints(900L);
        userRepository.save(user);

        // Vamos conceder 3000 XP (100 fixo + 2900 de um treino com muito volume)
        // Isso deve levá-lo do nível 1 para o 3.
        // Nível 2: 1000 XP
        // Nível 3: 2500 XP (500 * (3-1)^2 + 1000 * (3-1)) = 500*4 + 2000 = 4000
        // XP total final: 900 (inicial) + 3000 (ganho) = 3900
        // Com 3900 de XP total, ele deve ser nível 2, pois não atingiu os 4000 para o nível 3.
        // Vamos ajustar o ganho para 3100 XP para garantir a subida para o nível 3
        long xpGained = 3100L;

        // --- 2. AÇÃO (Act) ---

        // Simulamos o ganho de XP diretamente no serviço.
        // Para este teste, não precisamos simular a chamada de API de treino,
        // pois queremos focar apenas na lógica de cálculo de nível e exibição.
        user.setExperiencePoints(user.getExperiencePoints() + xpGained);
        gamificationService.awardXpForWorkout(user, new com.musclebuilder.model.WorkoutLog()); // Passamos um log vazio
        userRepository.save(user);

        // Agora, simulamos o frontend chamando a API da dashboard
        MvcResult dashboardResult = mockMvc.perform(get("/api/dashboard"))
                .andExpect(status().isOk())
                .andReturn();

        // --- 3. VERIFICAÇÃO (Assert) ---

        // Convertemos a resposta JSON da dashboard para nosso DTO
        String dashboardJson = dashboardResult.getResponse().getContentAsString();
        DashboardDTO dashboard = objectMapper.readValue(dashboardJson, DashboardDTO.class);

        // Verificação 1: O usuário subiu para o nível correto?
        // XP total: 900 + 3100 = 4000.
        // Nível 3 requer 2500 XP, Nível 4 requer 5000 XP. Com 4000, ele deve ser Nível 3.
        assertThat(dashboard.userLevel().level()).isEqualTo(3);

        // Verificação 2: A barra de progresso está correta?
        long xpTotalParaNivel3 = gamificationService.getTotalXpForLevel(3); // Deverá retornar 2500
        long xpTotalParaNivel4 = gamificationService.getTotalXpForLevel(4); // Deverá retornar 5000

        long xpNecessarioParaEsteNivel = xpTotalParaNivel4 - xpTotalParaNivel3; // 2500
        long progressoRealNesteNivel = user.getExperiencePoints() - xpTotalParaNivel3; // 4000 - 2500 = 1500

        // A API deve retornar exatamente esses valores calculados
        assertThat(dashboard.userLevel().currentXp()).isEqualTo(progressoRealNesteNivel);
        assertThat(dashboard.userLevel().xpForNextLevel()).isEqualTo(xpNecessarioParaEsteNivel);

        System.out.println("Teste de Level Up Múltiplo passou com sucesso!");
        System.out.println("Nível final: " + dashboard.userLevel().level());
        System.out.println("Progresso na barra: " + dashboard.userLevel().currentXp() + " / " + dashboard.userLevel().xpForNextLevel());
    }
}