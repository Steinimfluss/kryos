package net.kryos.render;

import java.awt.Color;

import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3x2f;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.cursor.CursorType;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.render.TextureSetup;
import net.minecraft.client.renderer.RenderPipelines;

public class KryosGraphicsExtractor {
	private GuiGraphicsExtractor graphics;
	
	public KryosGraphicsExtractor(GuiGraphicsExtractor graphics) {
		this.graphics = graphics;
	}
	
	private void innerFill(
			final RenderPipeline renderPipeline,
			final TextureSetup textureSetup,
			final float x0,
			final float y0,
			final float x1,
			final float y1,
			final int color1,
			@Nullable final Integer color2
		) {
		graphics.guiRenderState
				.addGuiElement(
					new PreciseColoredRectangleState(
						renderPipeline, textureSetup, new Matrix3x2f(graphics.pose()), x0, y0, x1, y1, color1, color2 != null ? color2 : color1, graphics.scissorStack.peek()
					)
				);
		}
	
	public void fill(float x0, float y0, float x1, float y1, final Color col) {
		if (x0 < x1) {
			float tmp = x0;
			x0 = x1;
			x1 = tmp;
		}

		if (y0 < y1) {
			float tmp = y0;
			y0 = y1;
			y1 = tmp;
		}

		this.innerFill(RenderPipelines.GUI, TextureSetup.noTexture(), x0, y0, x1, y1, col.getRGB(), null);
	}
	
	public void hLine(float x0, float y0, float x1, float width, Color color) {
	    float left = Math.min(x0, x1);
	    float right = Math.max(x0, x1);
	    fill(left, y0, right, y0 + width, color);
	}

	public void vLine(float x0, float y0, float y1, float width, Color color) {
	    float top = Math.min(y0, y1);
	    float bottom = Math.max(y0, y1);
	    fill(x0, top, x0 + width, bottom, color);
	}

	public void outline(float x0, float y0, float x1, float y1, float width, Color color) {
	    float left   = Math.min(x0, x1);
	    float right  = Math.max(x0, x1);
	    float top    = Math.min(y0, y1);
	    float bottom = Math.max(y0, y1);

	    hLine(left, top, right, width, color);

	    hLine(left, bottom - width, right, width, color);

	    vLine(left, top, bottom, width, color);

	    vLine(right - width, top, bottom, width, color);
	}

	public void requestCursor(final CursorType cursorType) {
		graphics.requestCursor(cursorType);
	}
	
	public void text(final Font font, @Nullable final String str, final float x, final float y, final Color color) {
		graphics.pose().pushMatrix();
		graphics.pose().translate(x, y);
		graphics.text(font, str, 0, 0, color.getRGB());
		graphics.pose().popMatrix();
	}

	public void centeredText(final Font font, final String str, final float x, final float y, final Color color) {
		graphics.text(font, str, (int)(x - font.width(str) / 2), (int)y, color.getRGB());
	}
}