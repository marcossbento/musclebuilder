package com.musclebuilder.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "mission_completions")
public class MissionCompletion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "mission_id", nullable = false)
    private String missionId;

    @Column(name = "completed_at", nullable = false)
    private LocalDateTime completedAt;

    public MissionCompletion() {}

    public MissionCompletion(User user, String missionId) {
        this.user = user;
        this.missionId = missionId;
    }

    @PrePersist // Garante a execução automática antes da entidade ser registrada no DB
    protected void onCreate() {
        this.completedAt = LocalDateTime.now();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MissionCompletion that = (MissionCompletion) o;
        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

}
