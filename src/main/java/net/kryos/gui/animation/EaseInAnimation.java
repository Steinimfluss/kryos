package net.kryos.gui.animation;

public class EaseInAnimation extends Animation {
    public EaseInAnimation(double start, double end, double duration) {
        super(start, end, duration);
    }
    
    public EaseInAnimation(double duration) {
        super(duration);
    }

    @Override
    protected double interpolate(double start, double end, double t) {
        return start + (end - start) * (t * t);
    }
}