package net.kryos.feature.setting;

public class NumberSettingBuilder<T extends Number & Comparable<T>> {
    private String name;
    private T value;
    public T min;
    public T max;
    public T step;

    public NumberSettingBuilder<T> name(String name) {
        this.name = name;
        return this;
    }

    public NumberSettingBuilder<T> value(T value) {
        this.value = value;
        return this;
    }

    public NumberSettingBuilder<T> min(T min) {
        this.min = min;
        return this;
    }

    public NumberSettingBuilder<T> max(T max) {
        this.max = max;
        return this;
    }

    public NumberSettingBuilder<T> step(T step) {
        this.step = step;
        return this;
    }

    public NumberSetting<T> build() {
        if (name == null) throw new IllegalStateException("NumberSetting requires name()");
        if (value == null) throw new IllegalStateException("NumberSetting requires value()");
        if (min == null)   throw new IllegalStateException("NumberSetting requires min()");
        if (max == null)   throw new IllegalStateException("NumberSetting requires max()");
        if (step == null)  throw new IllegalStateException("NumberSetting requires step()");
        
        NumberSetting<T> setting = new NumberSetting<>();
        setting.setName(name);
        setting.setMin(min);
        setting.setMax(max);
        setting.setStep(step);
        setting.setValue(value);

        return setting;
    }
}
