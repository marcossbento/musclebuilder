package com.musclebuilder.service;

import com.musclebuilder.dto.*;
import com.musclebuilder.exception.ResourceNotFoundException;
import com.musclebuilder.exception.UnauthorizedAccessException;
import com.musclebuilder.mapper.UserMapper;
import com.musclebuilder.model.Achievement;
import com.musclebuilder.model.User;
import com.musclebuilder.repository.AchievementRepository;
import com.musclebuilder.repository.UserRepository;
import com.musclebuilder.service.security.SecurityContextService;
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
    private final SecurityContextService securityContextService;
    private final UserMapper userMapper;

    @Autowired
    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       AchievementRepository achievementRepository,
                       SecurityContextService securityContextService,
                       UserMapper userMapper
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.achievementRepository = achievementRepository;
        this.securityContextService = securityContextService;
        this.userMapper = userMapper;
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
        return userMapper.toDto(savedUser);
    }

    public UserDTO getCurrentUserDetails() {
        User currentUser = securityContextService.findCurrentUser();
        return userMapper.toDto(currentUser);
    }

    public UserDTO updateUserProfile(UserUpdateDTO userUpdateDTO) {
        User userToUpdate = securityContextService.findCurrentUser();
        userToUpdate.setName(userUpdateDTO.name());
        userToUpdate.setHeight(userUpdateDTO.height());
        userToUpdate.setWeight(userUpdateDTO.weight());
        userToUpdate.setGoal(userUpdateDTO.goal());
        User updatedUser = userRepository.save(userToUpdate);

        return userMapper.toDto(updatedUser);
    }

    public void deleteCurrentUser() {
        User userToDelete = securityContextService.findCurrentUser();
        userRepository.delete(userToDelete);
    }
}