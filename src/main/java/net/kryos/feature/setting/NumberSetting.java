package net.kryos.feature.setting;

public class NumberSetting<T extends Number & Comparable<T>> extends Setting {
    private T value;
    public final T min;
    public final T max;
    public final T step;

    public NumberSetting(String name, T value, T min, T max, T step) {
        super(name);
        this.value = value;
        this.min = min;
        this.max = max;
        this.step = step;
    }

    public void setValue(T newValue) {
        setValueFromDouble(newValue.doubleValue());
    }

    public void setValueFromDouble(double d) {
        double minD = min.doubleValue();
        double maxD = max.doubleValue();
        double stepD = step.doubleValue();

        double snapped = Math.round((d - minD) / stepD) * stepD + minD;

        snapped = Math.max(minD, Math.min(maxD, snapped));

        this.value = cast(Math.round(snapped * 100.0) / 100.0);
    }


    @SuppressWarnings("unchecked")
    private T cast(double d) {
        if (value instanceof Integer) return (T) Integer.valueOf((int) d);
        if (value instanceof Long)    return (T) Long.valueOf((long) d);
        if (value instanceof Float)   return (T) Float.valueOf((float) d);
        if (value instanceof Double)  return (T) Double.valueOf(d);

        throw new IllegalArgumentException("Number type not implemented: " + value.getClass().getSimpleName());
    }

    public T getValue() {
        return value;
    }
}
