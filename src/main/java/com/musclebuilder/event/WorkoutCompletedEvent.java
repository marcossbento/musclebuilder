package com.musclebuilder.event;

import com.musclebuilder.model.Achievement;
import com.musclebuilder.model.WorkoutLog;

import java.util.ArrayList;
import java.util.List;

public class WorkoutCompletedEvent {
    private final WorkoutLog workoutLog;
    private final List<Achievement> newlyAwardedAchievements = new ArrayList<>();

    public WorkoutCompletedEvent(WorkoutLog workoutLog) {
        this.workoutLog = workoutLog;
    }

    public WorkoutLog getWorkoutLog() {
        return workoutLog;
    }

    public void addAchievements(List<Achievement> achievements) {
        this.newlyAwardedAchievements.addAll(achievements);
    }

    public List<Achievement> getNewlyAwardedAchievements() {
        return newlyAwardedAchievements;
    }
}
