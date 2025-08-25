package com.musclebuilder.controller;

import com.musclebuilder.model.Achievement;
import com.musclebuilder.service.AchievementService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/achievements")
public class AchievementController {

    private final AchievementService achievementService;

    private AchievementController(AchievementService achievementService) {
        this.achievementService = achievementService;
    }

    @GetMapping
    public ResponseEntity<List<Achievement>> getCurrentUserAchievements() {
        List<Achievement> achievements = achievementService.getCurrentUserAchievements();

        return ResponseEntity.ok(achievements);
    }
}
