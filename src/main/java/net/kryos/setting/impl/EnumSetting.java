package net.kryos.setting.impl;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import net.kryos.setting.Requirement;
import net.kryos.setting.Setting;
import net.minecraft.network.chat.Component;

public class EnumSetting<T extends Enum<?>> extends Setting<T> {
    private EnumSetting(String id, String name, T defaultValue, Component description) {
        super(id, name, defaultValue, description);
    }

    @Override
    public String getValueString() {
        return getValue().name();
    }
    
    @Override
    public void setValueString(String valueName) {
        Class<?> clazz = getValue().getDeclaringClass();
        @SuppressWarnings("unchecked")
        T[] constants = (T[]) clazz.getEnumConstants();

        for(T constant : constants) {
            if(constant.name().contentEquals(valueName)) {
                super.setValue(constant);
            }
        }
    }
    
    public List<T> getValues() {
        @SuppressWarnings("unchecked")
        T[] constants = (T[]) getValue().getDeclaringClass().getEnumConstants();
        return Arrays.asList(constants);
    }
    
    public void next() {
        List<T> values = getValues();
        int index = values.indexOf(getValue());
        int nextIndex = (index + 1) % values.size();
        super.setValue(values.get(nextIndex));
    }

    public void previous() {
        List<T> values = getValues();
        int index = values.indexOf(getValue());
        int prevIndex = (index - 1 + values.size()) % values.size();
        super.setValue(values.get(prevIndex));
    }
    
    public static EnumSettingBuilder<?> builder() {
        return new EnumSettingBuilder<>();
    }
    
    public static class EnumSettingBuilder<T extends Enum<?>> {
        private String id;
        private String name;
        private T defaultValue;
        private Optional<Requirement> requirement = Optional.empty();
        private Component description = Component.empty();

        public EnumSettingBuilder<T> id(String id) {
            this.id = id;
            return this;
        }

        public EnumSettingBuilder<T> name(String name) {
            this.name = name;
            return this;
        }

        public EnumSettingBuilder<T> defaultValue(T defaultValue) {
            this.defaultValue = defaultValue;
            return this;
        }
        
        public EnumSettingBuilder<T> description(Component description) {
            this.description = description;
            return this;
        }
        
        public EnumSettingBuilder<T> requirement(Requirement requirement) {
            this.requirement = Optional.of(requirement);
            return this;
        }

        public EnumSetting<T> build() {
            EnumSetting<T> enumSetting = new EnumSetting<>(id, name, defaultValue, description);
            if(requirement.isPresent())
                enumSetting.requires(requirement.get());
            return enumSetting;
        }
    }
}
