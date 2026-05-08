package net.kryos.setting.impl;

import java.util.Optional;

import net.kryos.setting.Requirement;
import net.kryos.setting.Setting;
import net.minecraft.network.chat.Component;

public class FloatSetting extends Setting<Float> {
    private float min;
    private float max;
    private float step;

    private FloatSetting(String id, String name, float defaultValue, float min, float max, float step, Component description) {
        super(id, name, defaultValue, description);
        this.min = min;
        this.max = max;
        this.step = step;
    }

    @Override
    public String getValueString() {
        return Float.toString(getValue());
    }

    @Override
    public void setValueString(String value) {
        try {
            float f = Float.parseFloat(value);
            setValue(clamp(f));
        } catch (NumberFormatException e) {}
    }

    @Override
    public void setValue(Float value) {
        super.setValue(clamp(value));
    }

    private float clamp(float v) {
        float snapped = min + Math.round((v - min) / step) * step;
        return Math.max(min, Math.min(max, snapped));
    }

    public void increment() {
        setValue(getValue() + step);
    }

    public void decrement() {
        setValue(getValue() - step);
    }

    public float getMin() { return min; }
    public float getMax() { return max; }
    public float getStep() { return step; }

    public void setMin(float min) { this.min = min; }
    public void setMax(float max) { this.max = max; }
    public void setStep(float step) { this.step = step; }

    public static class FloatSettingBuilder {
        private String id;
        private String name;
        private float defaultValue;
        private float min;
        private float max;
        private float step;
        private Optional<Requirement> requirement = Optional.empty();
        private Component description = Component.empty();

        public FloatSettingBuilder id(String id) {
            this.id = id;
            return this;
        }

        public FloatSettingBuilder name(String name) {
            this.name = name;
            return this;
        }

        public FloatSettingBuilder defaultValue(float defaultValue) {
            this.defaultValue = defaultValue;
            return this;
        }

        public FloatSettingBuilder min(float min) {
            this.min = min;
            return this;
        }

        public FloatSettingBuilder max(float max) {
            this.max = max;
            return this;
        }

        public FloatSettingBuilder step(float step) {
            this.step = step;
            return this;
        }

        public FloatSettingBuilder description(Component description) {
            this.description = description;
            return this;
        }
        
        public FloatSettingBuilder requirement(Requirement requirement) {
            this.requirement = Optional.of(requirement);
            return this;
        }

        public FloatSetting build() {
            FloatSetting floatSetting = new FloatSetting(id, name, defaultValue, min, max, step, description);
            if(requirement.isPresent())
                floatSetting.requires(requirement.get());
            return floatSetting;
        }
    }
}
