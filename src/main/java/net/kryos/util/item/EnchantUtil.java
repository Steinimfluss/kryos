package net.kryos.util.item;

import it.unimi.dsi.fastutil.objects.Object2IntMap.Entry;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemInstance;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;

public class EnchantUtil {
	public static int getEnchantmentLevel(ItemInstance stack, ResourceKey<Enchantment> enchantment) {
        ItemEnchantments enchantments = stack.get(DataComponents.ENCHANTMENTS);
        if(enchantments == null) return 0;
        
        for(Entry<Holder<Enchantment>> entry : enchantments.entrySet()) {
        	if(entry.getKey().is(enchantment)) {
        		return entry.getIntValue();
        	}
        }
        
        return 0;
	}
}