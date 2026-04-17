package net.kryos.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.kryos.Kryos;
import net.kryos.event.impl.ModifyFieldOfViewEvent;
import net.minecraft.client.player.AbstractClientPlayer;

@Mixin(AbstractClientPlayer.class)
public class AbstractClientPlayerMixin {

    @Inject(
        method = "getFieldOfViewModifier",
        at = @At("HEAD"),
        cancellable = true
    )
    private void getFieldOfViewModifier(boolean firstPerson, float effectScale, CallbackInfoReturnable<Float> cir) {
        ModifyFieldOfViewEvent event = new ModifyFieldOfViewEvent(firstPerson, effectScale);
        Kryos.eventBus.post(event);

        if (event.isModified()) {
            cir.setReturnValue(event.getFov());
        }
    }
}
