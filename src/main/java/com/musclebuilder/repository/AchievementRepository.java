package com.musclebuilder.repository;

import com.musclebuilder.model.Achievement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AchievementRepository extends JpaRepository<Achievement, Long> {

    List<Achievement> findByUserId(Long userId);

    boolean existsByUserIdAndName(Long userId, String achievementName);

}
