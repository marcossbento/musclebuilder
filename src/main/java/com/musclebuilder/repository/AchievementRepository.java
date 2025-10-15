package com.musclebuilder.repository;

import com.musclebuilder.model.Achievement;
import com.musclebuilder.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AchievementRepository extends JpaRepository<Achievement, Long> {

    List<Achievement> findByUser(User user);

    long countByUser(User user);

    boolean existsByUserAndName(User user, String achievementName);

    Optional<Achievement> findFirstByUserOrderByEarnedAtDesc(User user);
}
