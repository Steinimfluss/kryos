package net.kryos.feature.setting;

public class NumberSetting<T extends Number & Comparable<T>> extends Setting {
    private T value;
    private T min;
    private T max;
    private T step;
    private Class<T> type;

    public T getValue() {
        return value;
    }

    public T getMin() {
        return min;
    }

    public T getMax() {
        return max;
    }

    public T getStep() {
        return step;
    }

    public void setMin(T min) {
        this.min = min;
        inferType(min);
        if (value != null) {
            this.value = clamp(value);
        }
    }

    public void setMax(T max) {
        this.max = max;
        inferType(max);
        if (value != null) {
            this.value = clamp(value);
        }
    }

    public void setStep(T step) {
        this.step = step;
        inferType(step);
        if (value != null) {
            this.value = clamp(value);
        }
    }

    public void setValue(T value) {
        inferType(value);
        this.value = clamp(value);
    }

    public void setValueFromDouble(double d) {
        if (type == null)
            throw new IllegalStateException("NumberSetting type not inferred yet");

        T converted = convert(d);
        this.value = clamp(converted);
    }

    @SuppressWarnings("unchecked")
    private T convert(double d) {
        if (type.equals(Integer.class)) return (T) Integer.valueOf((int) d);
        if (type.equals(Double.class))  return (T) Double.valueOf(d);
        if (type.equals(Float.class))   return (T) Float.valueOf((float) d);
        if (type.equals(Long.class))    return (T) Long.valueOf((long) d);
        if (type.equals(Short.class))   return (T) Short.valueOf((short) d);
        if (type.equals(Byte.class))    return (T) Byte.valueOf((byte) d);

        throw new IllegalStateException("Unsupported number type: " + type);
    }

    @SuppressWarnings("unchecked")
    private void inferType(T sample) {
        if (sample != null && type == null) {
            this.type = (Class<T>) sample.getClass();
        }
    }

    private T clamp(T v) {
        if (v == null) return null;

        if (min != null && v.compareTo(min) < 0) v = min;
        if (max != null && v.compareTo(max) > 0) v = max;

        if (step == null || type == null) return v;

        double base = (min != null) ? min.doubleValue() : 0.0;
        double raw  = v.doubleValue();
        double st   = step.doubleValue();

        double snapped = base + Math.round((raw - base) / st) * st;

        return convert(snapped);
    }

    @Override
    public String toString() {
        if (value == null) return "null";
        if (type == null)  return value.toString();

        if (type.equals(Long.class)) {
            long ms = ((Number) value).longValue();

            long totalSeconds = ms / 1000;
            long minutes = totalSeconds / 60;
            long hours   = minutes / 60;

            long seconds = totalSeconds % 60;
            minutes %= 60;

            long millis = ms % 1000;

            if (hours > 0)
                return String.format("%d:%02d:%02d.%03d", hours, minutes, seconds, millis);
            else
                return String.format("%d:%02d.%03d", minutes, seconds, millis);
        }

        if (type.equals(Float.class) || type.equals(Double.class)) {
            return String.format("%.2f", value.doubleValue());
        }

        return value.toString();
    }
}