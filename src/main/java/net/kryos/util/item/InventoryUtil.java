package net.kryos.util.item;

import java.util.Optional;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.ServerboundSetCarriedItemPacket;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

public class InventoryUtil {
	private static final Minecraft mc = Minecraft.getInstance();
	
	public static ItemStack findFastestTool(BlockState state, BlockPos pos) {
        float bestScore = 1;
        ItemStack bestStack = mc.player.getActiveItem();

        for (int i = 0; i < 9; i++) {
            ItemStack stack = mc.player.getInventory().getItem(i);
            if (!stack.canDestroyBlock(state, mc.level, pos, mc.player)) continue;

            float score = stack.getDestroySpeed(state);
            if (score > bestScore) {
                bestScore = score;
                bestStack = stack;
            }
        }

        return bestStack;
    }
	
	public static Optional<Integer> getSlotWithStack(ItemStack stack) {
		for (int i = 0; i < 9; i++) {
            ItemStack s = mc.player.getInventory().getItem(i);
            
            if(stack == s) {
            	return Optional.of(i);
            }
        }
		return Optional.empty();
	}
	
	public static Optional<Integer> getSlotWithItem(Item item) {
		for (int i = 0; i < Inventory.INVENTORY_SIZE; i++) {
            ItemStack s = mc.player.getInventory().getItem(i);
            
            if(s.getItem() == item) {
            	return Optional.of(i);
            }
        }
		return Optional.empty();
	}
	
	public static boolean hasItemIn(Item item, InteractionHand hand) {
		return mc.player.getItemInHand(hand) != null && mc.player.getItemInHand(hand).getItem() == item;
	}
	
	public static boolean hasStackIn(ItemStack stack, InteractionHand hand) {
		return mc.player.getItemInHand(hand) == stack;
	}
	
	public static void slotUpdate(int slot) {
		mc.getConnection().send(new ServerboundSetCarriedItemPacket(slot));
	}
}	