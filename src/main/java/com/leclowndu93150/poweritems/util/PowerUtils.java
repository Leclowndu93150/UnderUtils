package com.leclowndu93150.poweritems.util;

public class PowerUtils {
    public static final int TICKS_PER_MINUTE = 1200;

    public static int minutesToTicks(int minutes) {
        return minutes * TICKS_PER_MINUTE;
    }

    public static int ticksToMinutes(int ticks) {
        return ticks / TICKS_PER_MINUTE;
    }

    public static int secondsToTicks(int seconds) {
        return seconds * 20;
    }

    public static int getTimeBarWidth(int currentTicks, int maxTicks) {
        return Math.round(13.0F * currentTicks / maxTicks);
    }

    public static String formatTimeRemaining(int ticks) {
        int hours = ticks / (TICKS_PER_MINUTE * 60);
        int minutes = (ticks % (TICKS_PER_MINUTE * 60)) / TICKS_PER_MINUTE;
        int seconds = (ticks % TICKS_PER_MINUTE) / 20;

        if (hours > 0) {
            return String.format("%02d:%02d:%02d", hours, minutes, seconds);
        } else if (minutes > 0) {
            return String.format("%02d:%02d", minutes, seconds);
        } else {
            return String.format("%02d", seconds);
        }
    }
}
