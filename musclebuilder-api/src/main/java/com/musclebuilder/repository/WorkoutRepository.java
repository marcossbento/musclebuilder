package com.musclebuilder.repository;

import com.musclebuilder.model.Workout;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WorkoutRepository extends JpaRepository<Workout, Long> {

    List<Workout> findByUserId(Long userId);

    List<Workout> findByUserIdAndWorkoutType(Long userId, String workoutType);

    @Query("SELECT w FROM Workout w WHERE w.user.id = :userId ORDER BY w.createdAt DESC")
    List<Workout> findRecentWorkoutsByUserId(@Param("userId") Long userId);

    @Query("SELECT COUNT(w) > 0 FROM Workout w WHERE w.id = :workoutId AND w.user.id = :userId")
    boolean existsByIdAndUserId(@Param("workoutId") Long workoutId, @Param("userId") Long userId);

}
