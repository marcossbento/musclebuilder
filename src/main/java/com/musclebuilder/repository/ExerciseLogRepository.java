package com.musclebuilder.repository;

import com.musclebuilder.model.ExerciseLog;
import com.musclebuilder.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ExerciseLogRepository extends JpaRepository<ExerciseLog, Long> {

    @Query("SELECT el FROM ExerciseLog el WHERE el.workoutLog.user = :user AND el.exercise.id = :exerciseId ORDER BY el.createdAt DESC")
    List<ExerciseLog> findExerciseHistoryForUser(@Param("user") User user, @Param("exerciseId") Long exerciseId);

    @Query("SELECT SUM(el.volume) FROM ExerciseLog el WHERE el.workoutLog.user = :user")
    Double findTotalVolumeByUser(@Param("user") User user);

    @Query("SELECT el.exerciseName FROM ExerciseLog el WHERE el.workoutLog.user = :user GROUP BY el.exerciseName ORDER BY COUNT(el.workoutName) DESC LIMIT 1")
    String findMostFrequentExerciseByUser(@Param("user") User user);
}
