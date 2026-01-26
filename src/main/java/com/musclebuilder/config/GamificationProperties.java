package com.musclebuilder.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "app.gamification")
public class GamificationProperties {

    private long xpPerWorkout = 150;
    private double xpPerVolumeUnit = 0.5;
    private double volumeXpCapRatio = 2.0;
    private DailyModifier dailyModifier = new DailyModifier();

    public long getXpPerWorkout() {
        return xpPerWorkout;
    }

    public void setXpPerWorkout(long xpPerWorkout) {
        this.xpPerWorkout = xpPerWorkout;
    }

    public double getXpPerVolumeUnit() {
        return xpPerVolumeUnit;
    }

    public void setXpPerVolumeUnit(double xpPerVolumeUnit) {
        this.xpPerVolumeUnit = xpPerVolumeUnit;
    }

    public double getVolumeXpCapRatio() {
        return volumeXpCapRatio;
    }

    public void setVolumeXpCapRatio(double volumeXpCapRatio) {
        this.volumeXpCapRatio = volumeXpCapRatio;
    }

    public DailyModifier getDailyModifier() {
        return dailyModifier;
    }

    public void setDailyModifier(DailyModifier dailyModifier) {
        this.dailyModifier = dailyModifier;
    }

    public static class DailyModifier {
        private double firstWorkout = 1.0;
        private double secondWorkout = 0.5;
        private double subsequentWorkouts = 0.1;

        public double getFirstWorkout() {
            return firstWorkout;
        }

        public void setFirstWorkout(double firstWorkout) {
            this.firstWorkout = firstWorkout;
        }

        public double getSecondWorkout() {
            return secondWorkout;
        }

        public void setSecondWorkout(double secondWorkout) {
            this.secondWorkout = secondWorkout;
        }

        public double getSubsequentWorkouts() {
            return subsequentWorkouts;
        }

        public void setSubsequentWorkouts(double subsequentWorkouts) {
            this.subsequentWorkouts = subsequentWorkouts;
        }
    }
}
