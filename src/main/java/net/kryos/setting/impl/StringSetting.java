package net.kryos.setting.impl;

import net.kryos.setting.Setting;

public class StringSetting extends Setting<String> {

    private StringSetting(String id, String name, String defaultValue) {
        super(id, name, defaultValue);
    }

    @Override
    public String getValueString() {
        return getValue();
    }

    @Override
    public void setValueString(String value) {
        setValue(value);
    }

    public static class StringSettingBuilder {
        private String id;
        private String name;
        private String defaultValue = "";

        public StringSettingBuilder id(String id) {
            this.id = id;
            return this;
        }

        public StringSettingBuilder name(String name) {
            this.name = name;
            return this;
        }

        public StringSettingBuilder defaultValue(String defaultValue) {
            this.defaultValue = defaultValue;
            return this;
        }

        public StringSetting build() {
            return new StringSetting(id, name, defaultValue);
        }
    }
}