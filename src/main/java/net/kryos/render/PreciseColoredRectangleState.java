package net.kryos.render;

import org.joml.Matrix3x2fc;
import org.jspecify.annotations.Nullable;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.render.TextureSetup;
import net.minecraft.client.renderer.state.gui.GuiElementRenderState;

public record PreciseColoredRectangleState(
    RenderPipeline pipeline,
    TextureSetup textureSetup,
    Matrix3x2fc pose,
    float x0,
    float y0,
    float x1,
    float y1,
    int col1,
    int col2,
    @Nullable ScreenRectangle scissorArea,
    ScreenRectangle bounds
) implements GuiElementRenderState {

    public PreciseColoredRectangleState(
        final RenderPipeline pipeline,
        final TextureSetup textureSetup,
        final Matrix3x2fc pose,
        final float x0,
        final float y0,
        final float x1,
        final float y1,
        final int col1,
        final int col2,
        @Nullable final ScreenRectangle scissorArea
    ) {
        this(
            pipeline,
            textureSetup,
            pose,
            x0, y0, x1, y1,
            col1, col2,
            scissorArea,
            getBounds(x0, y0, x1, y1, pose, scissorArea)
        );
    }

    @Override
    public void buildVertices(final VertexConsumer vertexConsumer) {
        vertexConsumer.addVertexWith2DPose(this.pose(), this.x0(), this.y0()).setColor(this.col1());
        vertexConsumer.addVertexWith2DPose(this.pose(), this.x0(), this.y1()).setColor(this.col2());
        vertexConsumer.addVertexWith2DPose(this.pose(), this.x1(), this.y1()).setColor(this.col2());
        vertexConsumer.addVertexWith2DPose(this.pose(), this.x1(), this.y0()).setColor(this.col1());
    }

    @Nullable
    private static ScreenRectangle getBounds(
        final float x0, final float y0, final float x1, final float y1,
        final Matrix3x2fc pose,
        @Nullable final ScreenRectangle scissorArea
    ) {
        PreciseScreenRectangle precise =
            new PreciseScreenRectangle(x0, y0, x1 - x0, y1 - y0)
                .transformMaxBounds(pose);

        ScreenRectangle vanillaBounds = precise.toScreenRectangle();

        return scissorArea != null
            ? scissorArea.intersection(vanillaBounds)
            : vanillaBounds;
    }
}
