package com.musclebuilder.controller;

import com.musclebuilder.dto.*;
import com.musclebuilder.mapper.AchievementMapper;
import com.musclebuilder.model.Achievement;
import com.musclebuilder.service.AchievementService;
import com.musclebuilder.service.CredentialsService;
import com.musclebuilder.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final AchievementService achievementService;
    private final CredentialsService credentialsService;
    private final AchievementMapper achievementMapper;

    @Autowired
    public UserController(
                            UserService userService,
                            AchievementService achievementService,
                            CredentialsService credentialsService,
                            AchievementMapper achievementMapper
                         )
    {
        this.userService = userService;
        this.achievementService = achievementService;
        this.credentialsService = credentialsService;
        this.achievementMapper = achievementMapper;
    }

    // == ENDPOINTS PÚBLICOS
    @PostMapping("/")
    public ResponseEntity<UserDTO> registerUser(@Valid @RequestBody UserRegistrationDTO userRegistrationDTO) {
        UserDTO savedUser = userService.registerUser(userRegistrationDTO);
        return new ResponseEntity<>(savedUser, HttpStatus.CREATED);
    }

    // == ENDPOINTS AUTENTICADOS
    @GetMapping("/me")
    public ResponseEntity<UserDTO> getCurrenUserDetails() {
        UserDTO user = userService.getCurrentUserDetails();
        return ResponseEntity.ok(user);
    }

    @PutMapping("/me")
    public ResponseEntity<UserDTO> updateCurrentUserProfile(@Valid @RequestBody UserUpdateDTO userUpdateDTO) {
        UserDTO updatedUser = userService.updateUserProfile(userUpdateDTO);
        return ResponseEntity.ok(updatedUser);
    }

    @GetMapping("/me/achievements")
    public ResponseEntity<List<AchievementDTO>> getCurrentUserAchievements() {
        List<Achievement> achievements = achievementService.getCurrentUserAchievements();

        List<AchievementDTO> achievementDTOs = achievements.stream()
                .map(achievementMapper::toDto)
                .toList();

        return ResponseEntity.ok(achievementDTOs);
    }

    @PatchMapping("/me/email")
    public ResponseEntity<UserDTO> updateCurrentUserEmail(@Valid @RequestBody EmailUpdateDTO emailUpdateDTO) {
        UserDTO updatedUser = credentialsService.updateEmail(emailUpdateDTO);
        return ResponseEntity.ok(updatedUser);
    }

    @PatchMapping("/me/password")
    public ResponseEntity<Void> updateCurrentUserPassword(@Valid @RequestBody PasswordUpdateDTO passwordUpdateDTO) {
        credentialsService.updatePassword(passwordUpdateDTO);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/me")
    public ResponseEntity<Void> deleteCurrentUser() {
        userService.deleteCurrentUser();
        return ResponseEntity.noContent().build();
    }

    /*TODO Endpoint ROLE-BASED ADMIN
    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
        UserDTO user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        List<UserDTO> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }
    */
}
