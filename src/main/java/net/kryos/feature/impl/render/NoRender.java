package net.kryos.feature.impl.render;

import net.kryos.Kryos;
import net.kryos.event.impl.CameraBobEvent;
import net.kryos.event.impl.CameraHurtEvent;
import net.kryos.event.impl.ModifyFieldOfViewEvent;
import net.kryos.event.listener.impl.CameraBobListener;
import net.kryos.event.listener.impl.CameraHurtListener;
import net.kryos.event.listener.impl.ModifyFieldOfViewListener;
import net.kryos.feature.Feature;
import net.kryos.feature.FeatureCategory;
import net.kryos.feature.setting.BooleanSetting;
import net.kryos.feature.setting.BooleanSettingBuilder;

public class NoRender extends Feature implements ModifyFieldOfViewListener, CameraBobListener, CameraHurtListener {
	private BooleanSetting noFovModify = new BooleanSettingBuilder()
			.name("No FOV Modify")
			.value(true)
			.build();

	private BooleanSetting noHurtCam = new BooleanSettingBuilder()
			.name("No Hurt Cam")
			.value(true)
			.build();
	private BooleanSetting noBobCam = new BooleanSettingBuilder()
			.name("No Bob Cam")
			.value(true)
			.build();
	
	public NoRender() {
		super("NoRender", FeatureCategory.RENDER);
		setSettings(noFovModify, noHurtCam, noBobCam);
	}

	@Override
	protected void onEnable() {
		Kryos.eventBus.subscribe(this);
	}

	@Override
	protected void onDisable() {
		Kryos.eventBus.unsubscribe(this);
	}

	@Override
	public void modifyFOV(ModifyFieldOfViewEvent event) {
		if(noFovModify.enabled)
			event.setFov(1F);
	}

	@Override
	public void hurtCamera(CameraHurtEvent event) {
		if(noHurtCam.enabled)
			event.cancel();
	}

	@Override
	public void bobCamera(CameraBobEvent event) {
		if(noBobCam.enabled)
			event.cancel();
	}
}