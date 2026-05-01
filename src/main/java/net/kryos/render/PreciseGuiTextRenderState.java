package net.kryos.render;

import org.joml.Matrix3x2fc;
import org.jspecify.annotations.Nullable;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.renderer.state.gui.ScreenArea;
import net.minecraft.util.FormattedCharSequence;

public final class PreciseGuiTextRenderState implements ScreenArea {

    public final Font font;
    public final FormattedCharSequence text;
    public final Matrix3x2fc pose;
    public final float x;
    public final float y;
    public final int color;
    public final int backgroundColor;
    public final boolean dropShadow;
    final boolean includeEmpty;

    @Nullable
    public final ScreenRectangle scissor;

    private Font.PreparedText preparedText;

    @Nullable
    private ScreenRectangle bounds;

    public PreciseGuiTextRenderState(
        final Font font,
        final FormattedCharSequence text,
        final Matrix3x2fc pose,
        final float x,
        final float y,
        final int color,
        final int backgroundColor,
        final boolean dropShadow,
        final boolean includeEmpty,
        @Nullable final ScreenRectangle scissor
    ) {
        this.font = font;
        this.text = text;
        this.pose = pose;
        this.x = x;
        this.y = y;
        this.color = color;
        this.backgroundColor = backgroundColor;
        this.dropShadow = dropShadow;
        this.includeEmpty = includeEmpty;
        this.scissor = scissor;
    }

    public Font.PreparedText ensurePrepared() {
        if (this.preparedText == null) {
            this.preparedText = this.font.prepareText(
                this.text,
                this.x,
                this.y,
                this.color,
                this.dropShadow,
                this.includeEmpty,
                this.backgroundColor
            );

            ScreenRectangle vanillaBounds = this.preparedText.bounds();

            if (vanillaBounds != null) {
                PreciseScreenRectangle precise =
                    new PreciseScreenRectangle(
                        vanillaBounds.left(),
                        vanillaBounds.top(),
                        vanillaBounds.width(),
                        vanillaBounds.height()
                    ).transformMaxBounds(this.pose);

                ScreenRectangle transformed = precise.toScreenRectangle();

                this.bounds = this.scissor != null
                    ? this.scissor.intersection(transformed)
                    : transformed;
            }
        }

        return this.preparedText;
    }

    @Nullable
    @Override
    public ScreenRectangle bounds() {
        this.ensurePrepared();
        return this.bounds;
    }
}
