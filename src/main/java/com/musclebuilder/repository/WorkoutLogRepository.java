package com.musclebuilder.repository;

import com.musclebuilder.model.WorkoutLog;
import com.musclebuilder.model.WorkoutLogStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface WorkoutLogRepository extends JpaRepository<WorkoutLog, Long> {

    // Busca por usuário com paginação
    Page<WorkoutLog> findByUserIdOrderByStartedAtDesc(Long userId, Pageable pageable);

    // Busca logs por usuário e status
    List<WorkoutLog> findByUserIdAndStatusOrderByStartedAtDesc(Long userId, WorkoutLogStatus status);

    //Busca logs por período específico
    @Query("SELECT wl FROM WorkoutLog wl WHERE wl.user.id = :userId " +
            "AND wl.startedAt BETWEEN :startDate AND :endDate " +
            "ORDER BY wl.startedAt DESC")
    List<WorkoutLog> findByUserIdAndDateRange(@Param("userId") Long userId, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    //Estatísticas de treino por usuário
    @Query("SELECT COUNT(wl) FROM WorkoutLog wl WHERE wl.user.id = :userId AND wl.status = 'COMPLETED'")
    Long countCompletedWorkoutsByUserId(@Param("userId") Long userId);

    //Volume total por usuário
    @Query("SELECT COALESCE(SUM(wl.totalVolume), 0) FROM WorkoutLog wl " +
            "WHERE wl.user.id = :userId AND wl.status = 'COMPLETED'")
    Double getTotalVolumeByUserId(@Param("userId") Long userId);

    //Frequência semanal
    @Query("SELECT COUNT(wl) FROM WorkoutLog wl WHERE wl.user.id = :userId " +
            "AND wl.status = 'COMPLETED' " +
            "AND wl.startedAt >= :weekStart")
    Long getWeeklyWorkoutCount(@Param("userId") Long userId, @Param("weekStart") LocalDateTime weekStart);

    //Último treino completo
    Optional<WorkoutLog> findFirstByUserIdAndStatusOrderByCompletedAtDesc(Long userId, WorkoutLogStatus status);

    //Verifica se existe log em processo
    boolean existsByUserIdAndStatus(Long userId, WorkoutLogStatus status);

    //Busca logs baseados no template de treino original
    List<WorkoutLog> findByWorkoutIdOrderByStartedAtDesc(Long workoutId);

    @Query("SELECT wl FROM WorkoutLog wl LEFT JOIN FETCH wl.exerciseLogs WHERE wl.id = :id")
    Optional<WorkoutLog> findByIdWithExerciseLogs(@Param("id") Long id);

    @Query("SELECT wl FROM WorkoutLog wl LEFT JOIN FETCH wl.exerciseLogs WHERE wl.user.id = :userId")
    List<WorkoutLog> findByUserIdWithExerciseLogs(@Param("userId") Long userId);
}
