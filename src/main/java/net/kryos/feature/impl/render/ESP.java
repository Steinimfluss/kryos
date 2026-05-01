package net.kryos.feature.impl.render;

import java.awt.Color;

import com.mojang.blaze3d.vertex.PoseStack;

import net.kryos.Kryos;
import net.kryos.event.impl.RenderLevelEvent;
import net.kryos.event.listener.impl.RenderLevelListener;
import net.kryos.feature.Feature;
import net.kryos.feature.FeatureCategory;
import net.kryos.feature.setting.BooleanSetting;
import net.kryos.feature.setting.BooleanSettingBuilder;
import net.kryos.util.LevelRenderUtil;
import net.minecraft.client.Camera;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public class ESP extends Feature implements RenderLevelListener {
	private Color safe = new Color(0, 255, 0, 100);
	private Color harmful = new Color(255, 0, 0, 100);
	private Color other = new Color(0, 0, 255, 100);
	
	private BooleanSetting monsters = new BooleanSettingBuilder().name("Monsters").value(true).build();
	private BooleanSetting creatures = new BooleanSettingBuilder().name("Creatures").value(true).build();
	private BooleanSetting ambient = new BooleanSettingBuilder().name("Ambient").value(true).build();
	private BooleanSetting axolotls = new BooleanSettingBuilder().name("Axolotls").value(true).build();
	private BooleanSetting undergroundWaterCreatures = new BooleanSettingBuilder().name("Underground WC").value(true).build();
	private BooleanSetting waterCreatures = new BooleanSettingBuilder().name("Water Creatures").value(true).build();
	private BooleanSetting waterAmbient = new BooleanSettingBuilder().name("Water Ambient").value(true).build();
	private BooleanSetting misc = new BooleanSettingBuilder().name("Misc").value(true).build();

	
	public ESP() {
		super("ESP", FeatureCategory.RENDER);
		setSettings(monsters, creatures, ambient, axolotls, undergroundWaterCreatures, waterAmbient, misc);
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
	public void renderLevel(RenderLevelEvent event) {
	    for (Entity entity : mc.level.entitiesForRendering()) {
	        if (!(entity instanceof LivingEntity)) continue;
	        LivingEntity e = (LivingEntity) entity;

	        if (e == mc.player) continue;
	        if (e.isDeadOrDying()) continue;

	        Color color;

	        switch (e.getType().getCategory()) {
	            case CREATURE:
	                if (!creatures.enabled) continue;
	                color = safe;
	                break;

	            case WATER_CREATURE:
	                if (!waterCreatures.enabled) continue;
	                color = safe;
	                break;

	            case AMBIENT:
	                if (!ambient.enabled) continue;
	                color = other;
	                break;

	            case AXOLOTLS:
	                if (!axolotls.enabled) continue;
	                color = safe;
	                break;

	            case UNDERGROUND_WATER_CREATURE:
	                if (!undergroundWaterCreatures.enabled) continue;
	                color = safe;
	                break;

	            case WATER_AMBIENT:
	                if (!waterAmbient.enabled) continue;
	                color = other;
	                break;

	            case MONSTER:
	                if (!monsters.enabled) continue;
	                color = harmful;
	                break;

	            case MISC:
	                if (!misc.enabled) continue;
	                color = other;
	                break;

	            default:
	                continue;
	        }

	        Camera camera = mc.gameRenderer.getMainCamera();
	        PoseStack poseStack = new PoseStack();
	        poseStack.last().pose().mul(event.getModelViewMatrix());
	        poseStack.translate(
	            -camera.position().x,
	            -camera.position().y,
	            -camera.position().z
	        );
	        
	        poseStack.pushPose();
	        LevelRenderUtil.drawFilledBox(poseStack, entity.getBoundingBox(), color);
	        poseStack.popPose();
	    }
	}
}