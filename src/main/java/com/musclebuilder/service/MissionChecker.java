package com.musclebuilder.service;

import com.musclebuilder.event.WorkoutCompletedEvent;
import com.musclebuilder.model.User;

import java.util.Optional;

public interface MissionChecker {
    String getMissionId();

    String getDescription();

    long getXpReward();

    long getGoal();

    long getCurrentProgress(User user);

    Optional<Long> check(WorkoutCompletedEvent event);
}
