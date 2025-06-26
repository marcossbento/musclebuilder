package com.musclebuilder.service;

import com.musclebuilder.dto.UserDTO;
import com.musclebuilder.dto.UserRegistrationDTO;
import com.musclebuilder.model.User;
import com.musclebuilder.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Test
    void quandoRegistrarNovoUsuario_deveRetornarUsuarioSalvo() {
        //ARRANGE(Preparação)
        UserRegistrationDTO registrationDTO = new UserRegistrationDTO("Usuário teste", "teste@email.com", "senhaTeste", "180cm", "80kg", "Hipertrofia");

        when(userRepository.existsByEmail(anyString())).thenReturn(false);

        when(passwordEncoder.encode(anyString())).thenReturn("senha_criptografada_mock");

        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setId(1L);
            return user;
        });

        //ACT(Ação)

        UserDTO resultDTO = userService.registerUser(registrationDTO);

        //ASSERT(Verificação)

        assertThat(resultDTO).isNotNull();
        assertThat(resultDTO.id()).isEqualTo(1L);
        assertThat(resultDTO.name()).isEqualTo("Usuário teste");
        assertThat(resultDTO.email()).isEqualTo("teste@email.com");

        //Verifica se o método save foi chamado exatamente uma vez pelo Repo.
        verify(userRepository, times(1)).save(any(User.class));
    }

}
