package com.musclebuilder.repository;

import com.musclebuilder.model.User;
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

    Page<WorkoutLog> findByUserOrderByStartedAtDesc(User user, Pageable pageable);

    long countByUserAndStatus(User user, WorkoutLogStatus status);

    long countByUserAndStatusAndStartedAtAfter(User user, WorkoutLogStatus status, LocalDateTime startDate);

    long countByUserAndStatusAndCompletedAtAfter(User user, WorkoutLogStatus status, LocalDateTime completeDate);

    boolean existsByUserAndStatus(User user, WorkoutLogStatus status);

    List<WorkoutLog> findByUserAndStatusOrderByCompletedAtDesc(User user, WorkoutLogStatus status);

    Optional<WorkoutLog> findFirstByUserAndStatusOrderByCompletedAtDesc(User user, WorkoutLogStatus status);

    @Query("SELECT wl FROM WorkoutLog wl LEFT JOIN FETCH wl.exerciseLogs WHERE wl.user = :user")
    List<WorkoutLog> findByUserWithExerciseLogs(@Param("user") User user);

    @Query("SELECT wl FROM WorkoutLog wl LEFT JOIN FETCH wl.exerciseLogs el WHERE wl.id = :id AND wl.user = :user")
    Optional<WorkoutLog> findByIdAndUserWithExerciseLogs(@Param("id") Long id, @Param("user") User user);

}
