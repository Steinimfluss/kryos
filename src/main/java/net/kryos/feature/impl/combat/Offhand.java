package net.kryos.feature.impl.combat;

import net.kryos.Kryos;
import net.kryos.event.impl.player.PlayerTickEvent.Post;
import net.kryos.event.impl.player.PlayerTickEvent.Pre;
import net.kryos.event.listener.impl.player.PlayerTickListener;
import net.kryos.feature.Feature;
import net.kryos.feature.FeatureCategory;
import net.kryos.setting.Setting;
import net.kryos.setting.impl.BooleanSetting;
import net.kryos.setting.impl.FloatSetting;
import net.kryos.util.item.InventoryUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.item.Items;

public class Offhand extends Feature implements PlayerTickListener {

	private Setting<Boolean> totem = addSetting(new BooleanSetting.BooleanSettingBuilder()
			.id("totem")
			.name("Totem")
			.defaultValue(true)
			.build());
	
	private Setting<Float> totemThreshold = addSetting(new FloatSetting.FloatSettingBuilder()
			.id("totem_threshold")
			.name("Totem threshold")
			.min(1)
			.max(20)
			.step(0.5F)
			.defaultValue(14)
			.requirement(() -> totem.getValue())
			.build());
	
	private Setting<Boolean> crystal = addSetting(new BooleanSetting.BooleanSettingBuilder()
			.id("crystal")
			.name("Crystal")
			.build());
	
	public Offhand() {
		super("offhand", "Offhand", FeatureCategory.COMBAT, Component.literal("Places desired items into your offhand automatically"));
	}
	
	@Override
	protected void onEnable() {
		Kryos.eventBus.subscribe(this);
		
		super.onEnable();
	}
	
	@Override
	protected void onDisable() {
		Kryos.eventBus.unsubscribe(this);
		
		super.onDisable();
	}

	@Override
	public void onPre(Pre event) {
		if(totem.getValue() && mc.player.getHealth() <= totemThreshold.getValue()) {
			if(!InventoryUtil.hasItemIn(Items.TOTEM_OF_UNDYING, InteractionHand.OFF_HAND)) {
				InventoryUtil.getSlotWithItem(Items.TOTEM_OF_UNDYING).ifPresent(slot -> {
					mc.gameMode.handleContainerInput(mc.player.containerMenu.containerId, Inventory.isHotbarSlot(slot) ? slot + 36 : slot, Inventory.SLOT_OFFHAND, ContainerInput.SWAP, mc.player);
				});
			}
			return;
		}
		
		if(crystal.getValue() && !InventoryUtil.hasItemIn(Items.END_CRYSTAL, InteractionHand.OFF_HAND)) {
			InventoryUtil.getSlotWithItem(Items.END_CRYSTAL).ifPresent(slot -> {
				mc.gameMode.handleContainerInput(mc.player.containerMenu.containerId, Inventory.isHotbarSlot(slot) ? slot + 36 : slot, Inventory.SLOT_OFFHAND, ContainerInput.SWAP, mc.player);
			});
			return;
		}
	}

	@Override
	public void onPost(Post event) {
		
	}
}