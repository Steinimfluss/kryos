package net.kryos.feature.impl.misc;

import net.kryos.Kryos;
import net.kryos.event.impl.PlayerTickEvent.Post;
import net.kryos.event.impl.PlayerTickEvent.Pre;
import net.kryos.event.listener.impl.PlayerTickListener;
import net.kryos.feature.Feature;
import net.kryos.feature.FeatureCategory;
import net.kryos.rotation.RotationPrivilege;
import net.kryos.rotation.Rotator;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class MiddleClickFeature extends Feature implements Rotator, PlayerTickListener {
	public MiddleClickFeature() {
		super("MiddleClick", FeatureCategory.MISC);
	}

	@Override
	protected void onEnable() {
		Kryos.eventBus.subscribe(this);
		Kryos.rotationBus.unsubscribe(this);
	}

	@Override
	protected void onDisable() {
		Kryos.eventBus.unsubscribe(this);
		Kryos.rotationBus.unsubscribe(this);
	}

	@Override
	public void onPre(Pre event) {
		if(findXpSlot() == -1) return;
		
		Kryos.rotationBus.subscribe(this);
		
		if(mc.mouseHandler.isMiddlePressed()) {
			Kryos.rotationBus.subscribe(this);
			if(Kryos.rotationBus.rotate(mc.player.getYRot(), 90, this)) {
				int oldSlot = mc.player.getInventory().getSelectedSlot();
				mc.player.getInventory().setSelectedSlot(findXpSlot());
				float xRot = mc.player.getXRot();
				mc.player.setXRot(90);
				mc.gameMode.useItem(mc.player, InteractionHand.MAIN_HAND);
				mc.player.getInventory().setSelectedSlot(oldSlot);
				mc.player.setXRot(xRot);
			}
		}
	}
	
	private int findXpSlot() {
		for (int i = 0; i < Inventory.INVENTORY_SIZE; i++) {
			if(!Inventory.isHotbarSlot(i)) continue;
			
	    	ItemStack item  = mc.player.getInventory().getItem(i);
	        if (item.getItem() == Items.EXPERIENCE_BOTTLE) {
	            return i;
	        }
	    }

	    return -1;
	}

	@Override
	public void onPost(Post event) {
		
	}

	@Override
	public RotationPrivilege getRotationPrivilege() {
		return RotationPrivilege.LOWEST;
	}
}