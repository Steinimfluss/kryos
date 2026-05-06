package net.kryos.mixin.render;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.kryos.Kryos;
import net.kryos.event.impl.render.GuiExtractionEvent;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphicsExtractor;

@Mixin(Gui.class)
public abstract class GuiMixin {

    @Inject(
        method = "extractRenderState",
        at = @At("TAIL")
    )
    private void extractRenderState(
            GuiGraphicsExtractor graphics,
            DeltaTracker deltaTracker,
            CallbackInfo ci
    ) {
        GuiExtractionEvent event = new GuiExtractionEvent(graphics, deltaTracker);
        Kryos.eventBus.post(event);
    }
}
