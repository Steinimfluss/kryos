package net.kryos.shader;

import net.minecraft.client.renderer.rendertype.RenderSetup;
import net.minecraft.client.renderer.rendertype.RenderType;

public class KryosRenderTypes {
    private static final RenderType KRYOS_FILLED = RenderType.create(
        "kryos_filled",
        RenderSetup.builder(KryosRenderPipelines.DEBUG_QUADS)
            .sortOnUpload()
            .createRenderSetup()
    );

    public static RenderType filled() {
        return KRYOS_FILLED;
    }
}
