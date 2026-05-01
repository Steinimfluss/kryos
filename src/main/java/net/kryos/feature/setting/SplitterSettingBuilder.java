package net.kryos.feature.setting;

public class SplitterSettingBuilder {
    private final SplitterSetting setting;

    public SplitterSettingBuilder() {
        this.setting = new SplitterSetting();
    }

    public SplitterSettingBuilder name(String name) {
        setting.setName(name);
        return this;
    }

    public SplitterSettingBuilder setting(Setting child) {
        setting.addSettings(child);
        return this;
    }

    public SplitterSetting build() {
        if (setting.getName() == null) {
            throw new IllegalStateException("SplitterSetting requires name()");
        }

        return setting;
    }
}
