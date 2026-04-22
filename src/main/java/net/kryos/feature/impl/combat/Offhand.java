package net.kryos.feature.impl.combat;

import net.kryos.Kryos;
import net.kryos.event.impl.PlayerTickEvent.Post;
import net.kryos.event.impl.PlayerTickEvent.Pre;
import net.kryos.event.listener.impl.PlayerTickListener;
import net.kryos.feature.Feature;
import net.kryos.feature.FeatureCategory;
import net.kryos.feature.setting.BooleanSetting;
import net.kryos.feature.setting.NumberSetting;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameType;

public class Offhand extends Feature implements PlayerTickListener {
	private BooleanSetting crystalSetting = new BooleanSetting("Crystal");
	private BooleanSetting totemSetting = new BooleanSetting("Totem");
	private NumberSetting<Float> totemThreshold = new NumberSetting<Float>("Totem threshold", 10.0F, 0.0F, 40.0F, 0.5F);
	
	public Offhand() {
		super("Offhand", FeatureCategory.COMBAT);
		setSettings(crystalSetting, totemSetting, totemThreshold);
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
	public void onPre(Pre event) {
	    if (mc.player == null) return;
	    if(mc.gameMode.getPlayerMode() != GameType.SURVIVAL) return;
	    
	    if(totemSetting.enabled && mc.player.getHealth() <= totemThreshold.getValue()) {
		    if (mc.player.getOffhandItem().getItem() == Items.TOTEM_OF_UNDYING)
		        return;
	
		    int slot = findTotemSlot();
		    if (slot == -1) return;
		    mc.gameMode.handleContainerInput(mc.player.containerMenu.containerId, slot, Inventory.SLOT_OFFHAND, ContainerInput.SWAP, mc.player);
		    return;
	    }
	    
	    if(crystalSetting.enabled) {
			if(mc.player.getOffhandItem() != null && mc.player.getOffhandItem().getItem() != Items.END_CRYSTAL) {
			    int slot = findCrystalSlot();
			    if (slot == -1) return;
			    mc.gameMode.handleContainerInput(mc.player.containerMenu.containerId, slot, Inventory.SLOT_OFFHAND, ContainerInput.SWAP, mc.player);
		    	return;
			}
	    }
	}

	private int findCrystalSlot() {
		for (int i = 0; i < Inventory.INVENTORY_SIZE; i++) {
	    	ItemStack item  = mc.player.getInventory().getItem(i);
	        if (item.getItem() == Items.END_CRYSTAL) {
	            return i;
	        }
	    }

	    return -1;
	}

	private int findTotemSlot() {
		for (int i = 0; i < Inventory.INVENTORY_SIZE; i++) {
	    	ItemStack item  = mc.player.getInventory().getItem(i);
	        if (item.getItem() == Items.TOTEM_OF_UNDYING) {
	            return i;
	        }
	    }

	    return -1;
	}

	
	@Override
	public void onPost(Post event) {
		
	}
}