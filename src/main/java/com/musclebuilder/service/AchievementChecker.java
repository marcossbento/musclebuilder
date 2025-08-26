package com.musclebuilder.service;

import com.musclebuilder.model.Achievement;
import com.musclebuilder.model.User;

import java.util.Optional;

public interface AchievementChecker {
    Optional<Achievement> check(User user);
}
