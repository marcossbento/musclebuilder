package com.musclebuilder.controller;

import com.musclebuilder.dto.ExerciseProgressDTO;
import com.musclebuilder.dto.ProgressSummaryDTO;
import com.musclebuilder.model.User;
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
    public ResponseEntity<ProgressSummaryDTO> getSummary() {
        ProgressSummaryDTO summary = progressService.getSummaryForCurrentUser();
        return ResponseEntity.ok(summary);
    }

    @GetMapping("/exercises/{exerciseId}")
    public ResponseEntity<List<ExerciseProgressDTO>> getExerciseProgress(@PathVariable Long exerciseId, Authentication authentication) {
        List<ExerciseProgressDTO> history = progressService.getExerciseHistoryForUser(exerciseId);
        return ResponseEntity.ok(history);
    }
}
