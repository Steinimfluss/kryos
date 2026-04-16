package net.kryos.gui.animation;

public abstract class Animation {
    public double start;
    public double end;
    protected final double duration;
    protected double progress;

    public Animation(double start, double end, double duration) {
        this.start = start;
        this.end = end;
        this.duration = duration;
        this.progress = 0;
    }

    public Animation(double duration) {
        this(0, 0, duration);
    }

    public void update(double delta) {
        progress += delta;
        if (progress >= duration) {
            progress = duration;
        }
    }

    public double getValue() {
        double t = Math.max(0, Math.min(progress / duration, 1.0));
        return interpolate(start, end, t);
    }

    public void reset() {
        progress = 0;
    }

    public void setGoal(double newEnd) {
        this.start = getValue();
        this.end = newEnd;
        this.progress = 0;
    }

    protected abstract double interpolate(double start, double end, double t);
}