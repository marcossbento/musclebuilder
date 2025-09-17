package com.musclebuilder.service;

import com.musclebuilder.event.WorkoutCompletedEvent;

import java.util.Optional;

public interface MissionChecker {
    String getMissionId();

    Optional<Long> check(WorkoutCompletedEvent event);
}
