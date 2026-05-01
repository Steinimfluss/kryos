package net.kryos.feature.setting;

public class ModeSettingBuilder {
    private final ModeSetting setting;

    public ModeSettingBuilder() {
        this.setting = new ModeSetting();
    }

    public ModeSettingBuilder name(String name) {
        setting.setName(name);
        return this;
    }

    public ModeSettingBuilder value(String value) {
        setting.setValue(value);
        return this;
    }

    public ModeSettingBuilder value(ModeSettingValue value) {
        setting.setValue(value);
        return this;
    }

    public ModeSettingBuilder mode(String mode) {
        ModeSettingValue val = new ModeSettingValue();
        val.setName(mode);
        setting.addValue(val);
        return this;
    }

    public ModeSettingBuilder mode(ModeSettingValue mode) {
        setting.addValue(mode);
        return this;
    }

    public ModeSetting build() {
        if (setting.getName() == null) {
            throw new IllegalStateException("ModeSetting requires name()");
        }

        if (setting.getValues() == null || setting.getValues().isEmpty()) {
            throw new IllegalStateException("ModeSetting requires at least one mode()");
        }

        if (setting.getValue() == null) {
            throw new IllegalStateException("ModeSetting requires value()");
        }

        boolean matches = setting.getValues().stream()
                .anyMatch(v -> v.getName().equalsIgnoreCase(setting.getValue().getName()));

        if (!matches)
            throw new IllegalStateException("ModeSetting value() must be one of the added modes()");

        return setting;
    }
}
