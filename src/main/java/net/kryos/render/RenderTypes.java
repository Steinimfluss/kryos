package net.kryos.render;

import net.minecraft.client.renderer.rendertype.RenderSetup;
import net.minecraft.client.renderer.rendertype.RenderType;

public class RenderTypes {
    private static final RenderType FILLED = RenderType.create(
        "filled",
        RenderSetup.builder(RenderPipelines.DEBUG_QUADS)
            .sortOnUpload()
            .createRenderSetup()
    );

    public static RenderType filled() {
        return FILLED;
    }
}