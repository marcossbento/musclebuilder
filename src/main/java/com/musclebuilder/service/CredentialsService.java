package com.musclebuilder.service;

import com.musclebuilder.dto.EmailUpdateDTO;
import com.musclebuilder.dto.PasswordUpdateDTO;
import com.musclebuilder.dto.UserDTO;
import com.musclebuilder.exception.UnauthorizedAccessException;
import com.musclebuilder.mapper.UserMapper;
import com.musclebuilder.model.User;
import com.musclebuilder.repository.UserRepository;
import com.musclebuilder.service.security.SecurityContextService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CredentialsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final SecurityContextService securityContextService;
    private final UserMapper userMapper;

    public CredentialsService(UserRepository userRepository, PasswordEncoder passwordEncoder, SecurityContextService securityContextService, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.securityContextService = securityContextService;
        this.userMapper = userMapper;
    }

    public UserDTO updateEmail(EmailUpdateDTO emailUpdateDTO) {
        User user = securityContextService.findCurrentUser();

        if (!passwordEncoder.matches(emailUpdateDTO.currentPassword(), user.getPassword())) {
            throw new UnauthorizedAccessException("Senha incorreta");
        }

        if (userRepository.existsByEmail(emailUpdateDTO.newEmail()) && !user.getEmail().equals(emailUpdateDTO.newEmail())) {
            throw new IllegalStateException("Este e-mail já está em uso");
        }

        user.setEmail(emailUpdateDTO.newEmail());
        User updatedUser = userRepository.save(user);

        return userMapper.toDto(updatedUser);
    }

    public void updatePassword(PasswordUpdateDTO passwordUpdateDTO) {
        if (!passwordUpdateDTO.newPassword().equals(passwordUpdateDTO.confirmPassword())) {
            throw new IllegalArgumentException("As senhas não correspondem");
        }

        User user = securityContextService.findCurrentUser();
        if (!passwordEncoder.matches(passwordUpdateDTO.currentPassword(), user.getPassword())) {
            throw new UnauthorizedAccessException("Senha atual incorreta");
        }

        user.setPassword(passwordEncoder.encode(passwordUpdateDTO.newPassword()));
        userRepository.save(user);
    }

}
