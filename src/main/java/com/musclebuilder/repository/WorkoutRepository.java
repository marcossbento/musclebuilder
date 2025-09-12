package com.musclebuilder.repository;

import com.musclebuilder.model.User;
import com.musclebuilder.model.Workout;
import com.musclebuilder.model.WorkoutType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WorkoutRepository extends JpaRepository<Workout, Long> {

    List<Workout> findByUserOrderByNameAsc(User user);

    Optional<Workout> findByIdAndUser(Long workoutId, User user);

    List<Workout> findByWorkoutType(WorkoutType workoutType);

    @Query("SELECT COUNT(w) > 0 FROM Workout w WHERE w.id = :workoutId AND w.user.id = :userId")
    boolean existsByIdAndUserId(@Param("workoutId") Long workoutId, @Param("userId") Long userId);

}
