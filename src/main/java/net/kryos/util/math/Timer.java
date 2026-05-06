package net.kryos.util.math;

public class Timer {
    private long lastTimeMs;

    public Timer() {
        reset();
    }

    public void reset() {
        lastTimeMs = System.currentTimeMillis();
    }

    public boolean check(long ms) {
        return System.currentTimeMillis() - lastTimeMs >= ms;
    }
}