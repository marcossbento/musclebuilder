package com.musclebuilder.repository;

import com.musclebuilder.model.ExerciseLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ExerciseLogRepository extends JpaRepository<ExerciseLog, Long> {

    //Busca logs de exercício a partir do workoutLog
    List<ExerciseLog> findByWorkoutLogIdOrderByOrderPosition(Long workoutLogId);

    //Progresso de exercício específico por user
    @Query("SELECT el FROM ExerciseLog el " +
            "JOIN el.workoutLog wl " +
            "WHERE wl.user.id = :userId AND el.exercise.id = :exerciseId " +
            "AND wl.status = 'COMPLETED' " +
            "ORDER BY wl.completedAt DESC"
    )
    List<ExerciseLog> findExerciseProgressByUser(@Param("userId") Long userId, @Param("exerciseId") Long exerciseId);

    //Máximo peso por exercício
    @Query("SELECT MAX(el.maxWeight) FROM ExerciseLog el " +
            "JOIN el.workoutLog wl " +
            "WHERE wl.user.id = :userId AND el.exercise.id = :exerciseId " +
            "AND wl.status = 'COMPLETED'"
    )
    Double getMaxWeightForExercise(@Param("userId") Long userId, @Param("exerciseId") Long exerciseId);

    @Query("SELECT COALESCE(SUM(el.volume), 0) FROM ExerciseLog el " +
            "JOIN el.workoutLog wl " +
            "WHERE wl.user.id = :userId AND el.exercise.id = :exerciseId " +
            "AND wl.status = 'COMPLETED'"
    )
    Double getTotalVolumeForExercise(@Param("userId") Long userId, @Param("exerciseId") Long exerciseId);

    //Logs de exercício por período específico
    @Query("SELECT el FROM ExerciseLog el " +
            "JOIN el.workoutLog wl " +
            "WHERE wl.user.id = :userId " +
            "AND wl.startedAt BETWEEN :startDate AND :endDate " +
            "AND wl.status = 'COMPLETED' " +
            "ORDER BY wl.startedAt DESC"
    )
    List<ExerciseLog> findByUserAndDateRange(@Param("userId") Long userId, @Param("startDate")LocalDateTime startDate, @Param("endDate")LocalDateTime endDate);

    //Top exercícios por volume
    @Query("SELECT el.exercise.name, SUM(el.volume) as totalVolume " +
            "FROM ExerciseLog el " +
            "JOIN el.workoutLog wl " +
            "WHERE wl.user.id = :userId AND wl.status = 'COMPLETED' " +
            "GROUP BY el.exercise.id, el.exercise.name " +
            "ORDER BY totalVolume DESC"
    )
    List<Object[]> getTopExercisesByVolume(@Param("userId") Long userId);

    @Query("SELECT COALESCE(SUM(el.volume), 0.0) FROM ExerciseLog el WHERE el.workoutLog.user.id = :userId")
    double findTotalVolumeByUserId(@Param("userId") Long userId);

    @Query(value = "SELECT e.exercise_name FROM exercise_logs e WHERE e.workout_log_id IN (SELECT id FROM workout_logs WHERE user_id = :userId) GROUP BY e.exercise_name ORDER BY COUNT(e.exercise_name) DESC LIMIT 1", nativeQuery = true)
    String findMostFrequentExerciseByUserId(@Param("userId") Long userId);

    @Query("SELECT el FROM ExerciseLog el WHERE el.workoutLog.user.id = :userId AND el.exercise.id = :exerciseId ORDER BY el.createdAt ASC")
    List<ExerciseLog> findExerciseHistory(@Param("userId") Long userId, @Param("exerciseId") Long exerciseId);
}
