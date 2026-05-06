package net.kryos.setting.impl;

import java.util.Optional;

import net.kryos.setting.Requirement;
import net.kryos.setting.Setting;

public class BooleanSetting extends Setting<Boolean> {
    private BooleanSetting(String id, String name, boolean defaultValue) {
        super(id, name, defaultValue);
    }

    @Override
    public String getValueString() {
        return Boolean.toString(getValue());
    }

    @Override
    public void setValueString(String value) {
        String v = value.trim().toLowerCase();

        switch (v) {
            case "true":
                setValue(true);
                break;
            case "false":
                setValue(false);
                break;
            default:
                break;
        }
    }

    public void toggle() {
        setValue(!getValue());
    }
    
    public static class BooleanSettingBuilder {
        private String id;
        private String name;
        private boolean defaultValue;
        private Optional<Requirement> requirement = Optional.empty();

        public BooleanSettingBuilder id(String id) {
            this.id = id;
            return this;
        }

        public BooleanSettingBuilder name(String name) {
            this.name = name;
            return this;
        }

        public BooleanSettingBuilder defaultValue(boolean defaultValue) {
            this.defaultValue = defaultValue;
            return this;
        }

        public BooleanSettingBuilder requirement(Requirement requirement) {
            this.requirement = Optional.of(requirement);
            return this;
        }

        public BooleanSetting build() {
        	BooleanSetting booleanSetting = new BooleanSetting(id, name, defaultValue);
	    	if(requirement.isPresent())
	    		booleanSetting.requires(requirement.get());
            return booleanSetting;
        }
    }
}