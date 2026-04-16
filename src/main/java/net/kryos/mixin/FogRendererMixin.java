package net.kryos.mixin;

import org.joml.Vector4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.kryos.Kryos;
import net.kryos.event.impl.ComputeFogColorEvent;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.fog.FogRenderer;

@Mixin(FogRenderer.class)
public abstract class FogRendererMixin {

    @Inject(
        method = "computeFogColor",
        at = @At(
            value = "INVOKE",
            target = "Lorg/joml/Vector4f;set(FFFF)Lorg/joml/Vector4f;",
            shift = At.Shift.AFTER
        )
    )
    private void computeFogColor(
            Camera camera,
            float partialTicks,
            ClientLevel level,
            int renderDistance,
            float darkenWorldAmount,
            Vector4f dest,
            CallbackInfo ci
    ) {
        ComputeFogColorEvent event = new ComputeFogColorEvent(camera, partialTicks, level, renderDistance, darkenWorldAmount, dest);
        Kryos.eventBus.post(event);

        dest.set(event.getDest());
    }
}