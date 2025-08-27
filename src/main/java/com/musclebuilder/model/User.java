package com.musclebuilder.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    private String height;

    private String weight;

    private String goal;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Achievement> achievements = new ArrayList<>();

    @Column(name = "experience_points", nullable = false, columnDefinition = "BIGINT DEFAULT 0")
    private Long experiencePoints = 0L;

    @Column(name = "user_level", nullable = false, columnDefinition = "INTEGER DEFAULT 1")
    private Integer level = 1;

    public User() {
    }

    public User(final Long id, final String name, final String email, final String password, final String height, final String weight, final String goal, final LocalDateTime createdAt, final LocalDateTime updatedAt, final List<Achievement> achievements) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.height = height;
        this.weight = weight;
        this.goal = goal;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.achievements = achievements;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(final String email) {
        this.email = email;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(final String password) {
        this.password = password;
    }

    public String getHeight() {
        return this.height;
    }

    public void setHeight(final String height) {
        this.height = height;
    }

    public String getWeight() {
        return this.weight;
    }

    public void setWeight(final String weight) {
        this.weight = weight;
    }

    public String getGoal() {
        return this.goal;
    }

    public void setGoal(final String goal) {
        this.goal = goal;
    }

    public LocalDateTime getCreatedAt() {
        return this.createdAt;
    }

    public void setCreatedAt(final LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return this.updatedAt;
    }

    public void setUpdatedAt(final LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public List<Achievement> getAchievements() {
        return this.achievements;
    }

    public void setAchievements(final List<Achievement> achievements) {
        this.achievements = achievements;
    }

    public Long getExperiencePoints() {
        return experiencePoints;
    }

    public void setExperiencePoints(Long experiencePoints) {
        this.experiencePoints = experiencePoints;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final User user = (User) o;
        return Objects.equals(id, user.id) &&
                Objects.equals(email, user.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, email);
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", height='" + height + '\'' +
                ", weight='" + weight + '\'' +
                ", goal='" + goal + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}