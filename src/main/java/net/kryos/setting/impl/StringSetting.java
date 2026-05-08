package net.kryos.setting.impl;

import net.kryos.setting.Setting;
import net.minecraft.network.chat.Component;

public class StringSetting extends Setting<String> {

    private StringSetting(String id, String name, String defaultValue, Component description) {
        super(id, name, defaultValue, description);
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
        private Component description = Component.empty();

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

        public StringSettingBuilder description(Component description) {
            this.description = description;
            return this;
        }

        public StringSetting build() {
            return new StringSetting(id, name, defaultValue, description);
        }
    }
}
