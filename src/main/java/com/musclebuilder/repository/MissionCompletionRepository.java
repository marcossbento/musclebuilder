package com.musclebuilder.repository;

import com.musclebuilder.model.MissionCompletion;
import com.musclebuilder.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface MissionCompletionRepository extends JpaRepository<MissionCompletion, Long> {
    boolean existsByUserAndMissionIdAndCompletedAtAfter(User user, String missionId, LocalDateTime completedAt);
}
