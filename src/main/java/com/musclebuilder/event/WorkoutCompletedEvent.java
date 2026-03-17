package com.musclebuilder.event;

import com.musclebuilder.model.Achievement;
import com.musclebuilder.model.WorkoutLog;

import java.util.ArrayList;
import java.util.List;

public class WorkoutCompletedEvent {
    private final WorkoutLog workoutLog;
    private final List<Achievement> newlyAwardedAchievements = new ArrayList<>();
    private int personalRecordsCount;

    public WorkoutCompletedEvent(WorkoutLog workoutLog) {
        this.workoutLog = workoutLog;
    }

    public WorkoutLog getWorkoutLog() {
        return workoutLog;
    }

    public int getPersonalRecordsCount() {
        return personalRecordsCount;
    }

    public void setPersonalRecordsCount(int personalRecordsCount) {
        this.personalRecordsCount = personalRecordsCount;
    }

    public void addAchievements(List<Achievement> achievements) {
        this.newlyAwardedAchievements.addAll(achievements);
    }

    public List<Achievement> getNewlyAwardedAchievements() {
        return newlyAwardedAchievements;
    }
}
