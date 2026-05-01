package net.kryos.feature.setting;

public class BooleanSettingBuilder {
    private final BooleanSetting setting;

    public BooleanSettingBuilder() {
        this.setting = new BooleanSetting();
    }

    public BooleanSettingBuilder name(String name) {
        setting.setName(name);
        return this;
    }

    public BooleanSettingBuilder value(boolean enabled) {
        setting.enabled = enabled;
        return this;
    }

    public BooleanSettingBuilder setting(Setting child) {
        setting.addSettings(child);
        return this;
    }

    public BooleanSetting build() {
        if (setting.getName() == null) {
            throw new IllegalStateException("BooleanSetting requires name()");
        }

        if (setting.getSettings() != null) {
            for (Setting s : setting.getSettings()) {
                if (s == null) {
                    throw new IllegalStateException("BooleanSetting contains a null child Setting");
                }
            }
        }

        return setting;
    }
}
