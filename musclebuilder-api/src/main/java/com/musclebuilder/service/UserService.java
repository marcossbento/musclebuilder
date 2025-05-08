package com.musclebuilder.service;

import com.musclebuilder.dto.EmailUpdateDTO;
import com.musclebuilder.dto.PasswordUpdateDTO;
import com.musclebuilder.dto.UserDTO;
import com.musclebuilder.dto.UserRegistrationDTO;
import com.musclebuilder.exception.ResourceNotFoundException;
import com.musclebuilder.exception.UnauthorizedAccessException;
import com.musclebuilder.model.User;
import com.musclebuilder.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UserDTO registerUser(UserRegistrationDTO userRegistrationDTO) {

        if (userRepository.existsByEmail(userRegistrationDTO.getEmail())) {
            throw new IllegalStateException("Este e-mail já está em uso");
        }

        User user = new User();
        user.setName(userRegistrationDTO.getName());
        user.setEmail(userRegistrationDTO.getEmail());
        user.setPassword(passwordEncoder.encode(userRegistrationDTO.getPassword()));
        user.setHeight(userRegistrationDTO.getHeight());
        user.setWeight(userRegistrationDTO.getWeight());
        user.setGoal(userRegistrationDTO.getGoal());

        User savedUser = userRepository.save(user);
        return convertToDTO(savedUser);
    }

    public UserDTO getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado com id: " + id));

        return convertToDTO(user);
    }

    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public UserDTO updateUser(Long id, UserDTO userDTO) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado com id: " + id));

        user.setName(userDTO.getName());
        //atualização de e-mail e senha em métodos dedicados
        user.setHeight(userDTO.getHeight());
        user.setWeight(userDTO.getWeight());
        user.setGoal(userDTO.getGoal());

        User updatedUser = userRepository.save(user);
        return convertToDTO(updatedUser);
    }

    public UserDTO updateEmail(Long userId, EmailUpdateDTO emailUpdateDTO) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado com id: " + userId));

        if (!passwordEncoder.matches(emailUpdateDTO.getCurrentPassword(), user.getPassword())) {
            throw new UnauthorizedAccessException("Senha incorreta");
        }

        if (userRepository.existsByEmail(emailUpdateDTO.getNewEmail()) && !user.getEmail().equals(emailUpdateDTO.getNewEmail())) {
            throw new IllegalStateException("Este e-mail já está em uso");
        }

        user.setEmail(emailUpdateDTO.getNewEmail());
        User updatedUser = userRepository.save(user);

        return convertToDTO(updatedUser);
    }

    public void updatePassword(Long userId, PasswordUpdateDTO passwordUpdateDTO) {
        if (!passwordUpdateDTO.getNewPassword().equals(passwordUpdateDTO.getConfirmPassword())) {
            throw new IllegalArgumentException("As senhas não correspondem");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado com id: " + userId));

        if (!passwordEncoder.matches(passwordUpdateDTO.getCurrentPassword(), user.getPassword())) {
            throw new UnauthorizedAccessException("Senha atual incorreta");
        }

        user.setPassword(passwordEncoder.encode(passwordUpdateDTO.getNewPassword()));
        userRepository.save(user);
    }

    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("Usuário não encontrado com id: " + id);
        }

        userRepository.deleteById(id);
    }

    private UserDTO convertToDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setHeight(user.getHeight());
        dto.setWeight(user.getWeight());
        dto.setGoal(user.getGoal());
        return dto;
    }

}