package com.musclebuilder.repository;

import com.musclebuilder.model.Achievement;
import com.musclebuilder.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AchievementRepository extends JpaRepository<Achievement, Long> {

    List<Achievement> findByUser(User user);

    boolean existsByUserAndName(User user, String achievementName);

}
