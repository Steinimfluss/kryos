package net.kryos.mixin.network.accessor;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.world.phys.Vec3;

@Mixin(ClientboundSetEntityMotionPacket.class)
public interface ClientboundSetEntityMotionPacketAccessor {
    @Accessor("movement")
    Vec3 getMovement();
    
    @Mutable
    @Accessor("movement")
    void setMovement(Vec3 movement);
}