package com.musclebuilder.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "achievements")
public class Achievement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch =  FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "achievement_name", nullable = false)
    private String name;

    @Column(name = "achievement_description", nullable = false)
    private String description;

    @Column(name = "badge_url")
    private String badgeUrl;

    @Column(name = "earned_at", nullable = false)
    private LocalDateTime earnedAt;

    public Achievement() {}

    public Achievement(final Long id, final User user, final String name, final String description, final String badgeUrl, final LocalDateTime earnedAt) {
        this.id = id;
        this.user = user;
        this.name = name;
        this.description = description;
        this.badgeUrl = badgeUrl;
        this.earnedAt = earnedAt;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public User getUser() {
        return this.user;
    }

    public void setUser(final User user) {
        this.user = user;
    }

    public String getName() {
        return this.name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public String getBadgeUrl() {
        return this.badgeUrl;
    }

    public void setBadgeUrl(final String badgeUrl) {
        this.badgeUrl = badgeUrl;
    }

    public LocalDateTime getEarnedAt() {
        return this.earnedAt;
    }

    public void setEarnedAt(final LocalDateTime earnedAt) {
        this.earnedAt = earnedAt;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (null == o || this.getClass() != o.getClass()) return false;
        final Achievement that = (Achievement) o;
        return Objects.equals(this.name, that.name) &&
                Objects.equals(this.user != null ? this.user.getId() : null,
                that.user != null ? that.user.getId() : null);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.name, this.user != null ? this.user.getId() : null);
    }

    @PrePersist
    protected void onEarn() {
        earnedAt = LocalDateTime.now();
    }
}
