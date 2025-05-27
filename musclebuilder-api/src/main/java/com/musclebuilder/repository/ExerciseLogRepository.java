package com.musclebuilder.repository;

import com.musclebuilder.model.ExerciseLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ExerciseLogRepository extends JpaRepository<ExerciseLog, Long> {

    //Busca logs de exercício a partir do workoutLog
    List<ExerciseLog> findByWorkoutLogIdOrderByOrderPosition(Long workoutLogId);

    //Progresso de exercício específico por user
    @Query("SELECT el FROM ExerciseLog el" +
            "JOIN el.workoutLog wl " +
            "WHERE wl.user.id = :userId AND el.exercise.id = :exerciseId " +
            "AND wl.status = 'COMPLETED' " +
            "ORDER BY wl.completedAt DESC"
    )
    List<ExerciseLog> findExerciseProgressByUser(@Param("userId") Long userId, @Param("exerciseId") Long exerciseId);

    //Volume total por exercício
    @Query("SELECT COALESCE(SUM(el.volume), 0) FROM ExerciseLog el " +
            "JOIN el.workoutLog wl " +
            "WHERE wl.user.id = :userId AND el.exercise.id = :exerciseId " +
            "AND wl.status = 'COMPLETED'"
    )
    Double getMaxWeightForExercise(@Param("userId") Long userId, @Param("exerciseId") Long exerciseId);

    //Logs de exercício por período específico
    @Query("SELECT el FROM ExerciseLog el " +
            "JOIN el.workout wl " +
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
}
