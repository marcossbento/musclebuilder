package com.musclebuilder.controller;

import com.musclebuilder.dto.ExerciseProgressDTO;
import com.musclebuilder.dto.ProgressSummaryDTO;
import com.musclebuilder.service.ProgressService;
import com.musclebuilder.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/progress")
public class ProgressController {

    private final ProgressService progressService;
    private final UserService userService;

    @Autowired
    public ProgressController(ProgressService progressService, UserService userService) {
        this.progressService = progressService;
        this.userService = userService;
    }

    @GetMapping("/summary")
    public ResponseEntity<ProgressSummaryDTO> getSummary(Authentication authentication) {
        Long userId = getUserIdFromAuthentication(authentication);
        ProgressSummaryDTO summary = progressService.getGlobalSummary(userId);
        return ResponseEntity.ok(summary);
    }

    @GetMapping("/exercises/{exerciseId}")
    public ResponseEntity<List<ExerciseProgressDTO>> getExerciseProgress(@PathVariable Long exerciseId, Authentication authentication) {

        Long userId = getUserIdFromAuthentication(authentication);
        List<ExerciseProgressDTO> history = progressService.getExerciseHistory(userId, exerciseId);
        return ResponseEntity.ok(history);
    }

    private Long getUserIdFromAuthentication(Authentication authentication) {
        String userEmail = authentication.getName();
        return userService.getUserByEmail(userEmail).id();
    }
}
