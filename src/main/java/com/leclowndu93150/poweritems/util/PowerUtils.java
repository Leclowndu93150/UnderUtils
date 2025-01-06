package com.leclowndu93150.poweritems.util;

public class PowerUtils {
    public static final int TICKS_PER_MINUTE = 1200;

    public static int minutesToTicks(int minutes) {
        return minutes * TICKS_PER_MINUTE;
    }

    public static int ticksToMinutes(int ticks) {
        return ticks / TICKS_PER_MINUTE;
    }

    public static int getTimeBarWidth(int currentTicks, int maxTicks) {
        return Math.round(13.0F * currentTicks / maxTicks);
    }

    public static String formatTimeRemaining(int ticks) {
        int minutes = ticksToMinutes(ticks);
        int seconds = (ticks % TICKS_PER_MINUTE) / 20;
        return String.format("%02d:%02d", minutes, seconds);
    }
}
