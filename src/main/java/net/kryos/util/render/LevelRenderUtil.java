package net.kryos.util.render;

import org.joml.Matrix4f;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.kryos.render.RenderTypes;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class LevelRenderUtil {
	private static final Minecraft mc = Minecraft.getInstance();
	
	private static void buildLine(
	        PoseStack matrixStack,
	        Camera camera,
	        VertexConsumer bufferBuilder,
	        double x1, double y1, double z1,
	        double x2, double y2, double z2,
	        int color
	) {
	    PoseStack.Pose entry = matrixStack.last();
	    Matrix4f matrix4f = entry.pose();
	    Vec3 cameraPos = camera.position();

	    float[] c = ColorUtil.unpackColor(color);
	    float r = c[0], g = c[1], b = c[2], a = c[3];

	    bufferBuilder
	            .addVertex(matrix4f, (float)(x1 - cameraPos.x), (float)(y1 - cameraPos.y), (float)(z1 - cameraPos.z))
	            .setColor(r, g, b, a);

	    bufferBuilder
	            .addVertex(matrix4f, (float)(x2 - cameraPos.x), (float)(y2 - cameraPos.y), (float)(z2 - cameraPos.z))
	            .setColor(r, g, b, a);
	}
	
	public static void drawLine(
	        PoseStack matrixStack,
	        Camera camera,
	        double x1, double y1, double z1,
	        double x2, double y2, double z2,
	        int color
	) {
	    MultiBufferSource.BufferSource provider = mc.renderBuffers().bufferSource();
	    RenderType layer = RenderTypes.filled();
	    VertexConsumer vc = provider.getBuffer(layer);

	    buildLine(matrixStack, camera, vc, x1, y1, z1, x2, y2, z2, color);

	    provider.endBatch(layer);
	}

	public static void drawFilledBox(PoseStack poseStack, AABB box, int color) {
	    MultiBufferSource.BufferSource provider = mc.renderBuffers().bufferSource();
	    VertexConsumer vc = provider.getBuffer(RenderTypes.filled());

	    float[] c = ColorUtil.unpackColor(color);
	    float r = c[0], g = c[1], b = c[2], a = c[3];

	    Matrix4f mat = poseStack.last().pose();

	    float x1 = (float) box.minX;
	    float y1 = (float) box.minY;
	    float z1 = (float) box.minZ;
	    float x2 = (float) box.maxX;
	    float y2 = (float) box.maxY;
	    float z2 = (float) box.maxZ;

	    vc.addVertex(mat, x1, y1, z2).setColor(r, g, b, a);
	    vc.addVertex(mat, x2, y1, z2).setColor(r, g, b, a);
	    vc.addVertex(mat, x2, y2, z2).setColor(r, g, b, a);
	    vc.addVertex(mat, x1, y2, z2).setColor(r, g, b, a);

	    vc.addVertex(mat, x2, y1, z1).setColor(r, g, b, a);
	    vc.addVertex(mat, x1, y1, z1).setColor(r, g, b, a);
	    vc.addVertex(mat, x1, y2, z1).setColor(r, g, b, a);
	    vc.addVertex(mat, x2, y2, z1).setColor(r, g, b, a);

	    vc.addVertex(mat, x1, y1, z1).setColor(r, g, b, a);
	    vc.addVertex(mat, x1, y1, z2).setColor(r, g, b, a);
	    vc.addVertex(mat, x1, y2, z2).setColor(r, g, b, a);
	    vc.addVertex(mat, x1, y2, z1).setColor(r, g, b, a);

	    vc.addVertex(mat, x2, y1, z2).setColor(r, g, b, a);
	    vc.addVertex(mat, x2, y1, z1).setColor(r, g, b, a);
	    vc.addVertex(mat, x2, y2, z1).setColor(r, g, b, a);
	    vc.addVertex(mat, x2, y2, z2).setColor(r, g, b, a);

	    vc.addVertex(mat, x1, y2, z2).setColor(r, g, b, a);
	    vc.addVertex(mat, x2, y2, z2).setColor(r, g, b, a);
	    vc.addVertex(mat, x2, y2, z1).setColor(r, g, b, a);
	    vc.addVertex(mat, x1, y2, z1).setColor(r, g, b, a);

	    vc.addVertex(mat, x1, y1, z1).setColor(r, g, b, a);
	    vc.addVertex(mat, x2, y1, z1).setColor(r, g, b, a);
	    vc.addVertex(mat, x2, y1, z2).setColor(r, g, b, a);
	    vc.addVertex(mat, x1, y1, z2).setColor(r, g, b, a);

	    provider.endBatch(RenderTypes.filled());
	}
}