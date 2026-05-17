package net.kryos.mixin.level;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.kryos.Kryos;
import net.kryos.event.impl.entity.RemoveEntityEvent;
import net.kryos.event.impl.entity.SpawnEntityEvent;
import net.kryos.event.impl.level.UpdateBlockEvent;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

@Mixin(ClientLevel.class)
public class ClientLevelMixin {
	@Inject(method = "addEntity", at = @At("HEAD"))
	public void addEntity(final Entity entity, CallbackInfo ci) {
	    SpawnEntityEvent event = new SpawnEntityEvent(entity);
	    Kryos.eventBus.post(event);
	}
	
	@Inject(method = "removeEntity", at = @At("HEAD"))
	public void removeEntity(final int id, final Entity.RemovalReason reason, CallbackInfo ci) {
		RemoveEntityEvent event = new RemoveEntityEvent(id, reason);
	    Kryos.eventBus.post(event);
	}

	@Inject(method = "setServerVerifiedBlockState", at = @At("HEAD"))
	public void setServerVerifiedBlockState(final BlockPos pos, final BlockState blockState, @Block.UpdateFlags final int updateFlag, CallbackInfo ci) {
		UpdateBlockEvent event = new UpdateBlockEvent(pos, blockState, updateFlag);
		Kryos.eventBus.post(event);
	}
}