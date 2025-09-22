package com.musclebuilder.service;

import com.musclebuilder.dto.*;
import com.musclebuilder.exception.ResourceNotFoundException;
import com.musclebuilder.exception.UnauthorizedAccessException;
import com.musclebuilder.model.Achievement;
import com.musclebuilder.model.User;
import com.musclebuilder.repository.AchievementRepository;
import com.musclebuilder.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AchievementRepository achievementRepository;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, AchievementRepository achievementRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.achievementRepository = achievementRepository;
    }

    public UserDTO registerUser(UserRegistrationDTO userRegistrationDTO) {

        if (userRepository.existsByEmail(userRegistrationDTO.email())) {
            throw new IllegalStateException("Este e-mail já está em uso");
        }

        User user = new User();
        user.setName(userRegistrationDTO.name());
        user.setEmail(userRegistrationDTO.email());
        user.setPassword(passwordEncoder.encode(userRegistrationDTO.password()));
        user.setHeight(userRegistrationDTO.height());
        user.setWeight(userRegistrationDTO.weight());
        user.setGoal(userRegistrationDTO.goal());

        User savedUser = userRepository.save(user);
        return mapToDTO(savedUser);
    }

    public UserDTO getCurrentUserDetails() {
        User currentUser = findCurrentUser();
        return mapToDTO(currentUser);
    }

    /* MÉTODO ROLE-BASED PARA ADMIN TODO
    public UserDTO getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado com e-mail: " + email));

        return mapToDTO(user);
    }
    */

    public UserDTO updateUserProfile(UserUpdateDTO userUpdateDTO) {
        User userToUpdate = findCurrentUser();
        userToUpdate.setName(userUpdateDTO.name());
        userToUpdate.setHeight(userUpdateDTO.height());
        userToUpdate.setWeight(userUpdateDTO.weight());
        userToUpdate.setGoal(userUpdateDTO.goal());
        User updatedUser = userRepository.save(userToUpdate);
        return mapToDTO(updatedUser);
    }

    public UserDTO updateEmail(EmailUpdateDTO emailUpdateDTO) {
        User user = findCurrentUser();

        if (!passwordEncoder.matches(emailUpdateDTO.currentPassword(), user.getPassword())) {
            throw new UnauthorizedAccessException("Senha incorreta");
        }

        if (userRepository.existsByEmail(emailUpdateDTO.newEmail()) && !user.getEmail().equals(emailUpdateDTO.newEmail())) {
            throw new IllegalStateException("Este e-mail já está em uso");
        }

        user.setEmail(emailUpdateDTO.newEmail());
        User updatedUser = userRepository.save(user);

        return mapToDTO(updatedUser);
    }

    public void updatePassword(PasswordUpdateDTO passwordUpdateDTO) {
        if (!passwordUpdateDTO.newPassword().equals(passwordUpdateDTO.confirmPassword())) {
            throw new IllegalArgumentException("As senhas não correspondem");
        }

        User user = findCurrentUser();
        if (!passwordEncoder.matches(passwordUpdateDTO.currentPassword(), user.getPassword())) {
            throw new UnauthorizedAccessException("Senha atual incorreta");
        }

        user.setPassword(passwordEncoder.encode(passwordUpdateDTO.newPassword()));
        userRepository.save(user);
    }

    public void deleteCurrentUser() {
        User userToDelete = findCurrentUser();
        userRepository.delete(userToDelete);
    }

    // MÉTODOS AUXILIARES

    public User findCurrentUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username;
        if (principal instanceof UserDetails) {
            username = ((UserDetails) principal).getUsername();
        } else {
            username = principal.toString();
        }
        return  userRepository.findByEmail(username)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário autenticado não encontrado com o email: " + username));
    }

    private UserDTO mapToDTO(User user) {
        return new UserDTO(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getHeight(),
                user.getWeight(),
                user.getGoal()
        );
    }

    private AchievementDTO mapToAchievementDTO(Achievement achievement) {
        return new AchievementDTO(
                achievement.getName(),
                achievement.getDescription(),
                achievement.getBadgeUrl(),
                achievement.getEarnedAt()
        );
    }

}