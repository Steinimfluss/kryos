package net.kryos.feature;

import java.util.Optional;

import org.jetbrains.annotations.NotNull;

import net.kryos.lock.LockPrivilege;
import net.kryos.lock.Locker;
import net.kryos.setting.Setting;
import net.kryos.setting.impl.EnumSetting;
import net.minecraft.network.chat.Component;

public class LockingFeature extends Feature implements Locker {
	private Setting<LockPrivilege> lockPrivilege;
	
	public LockingFeature(@NotNull String id, @NotNull String name, @NotNull FeatureCategory category,
			Optional<Component> description, LockPrivilege privilege) {
		super(id, name, category, description);
		
		lockPrivilege = addSetting(new EnumSetting.EnumSettingBuilder<LockPrivilege>()
				.id("lock_privilege")
				.name("Privilege")
				.defaultValue(privilege)
				.build());
	}

	@Override
	public LockPrivilege getLockerPrivilege() {
		return lockPrivilege.getValue();
	}
}
