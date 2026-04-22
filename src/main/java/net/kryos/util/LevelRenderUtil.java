package net.kryos.util;

import java.awt.Color;

import org.joml.Matrix4f;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.kryos.shader.KryosRenderTypes;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class LevelRenderUtil {
	private static final Minecraft mc = Minecraft.getInstance();
	
	private static void buildLine(PoseStack matrixStack, Camera camera, VertexConsumer bufferBuilder, double x1,
			double y1, double z1, double x2, double y2, double z2, Color color) {
		PoseStack.Pose entry = matrixStack.last();
		Matrix4f matrix4f = entry.pose();
		Vec3 cameraPos = camera.position();

		float r = color.getRed();
		float g = color.getGreen();
		float b = color.getBlue();

		bufferBuilder
				.addVertex(matrix4f, (float) (x1 - cameraPos.x), (float) (y1 - cameraPos.y), (float) (z1 - cameraPos.z))
				.setColor(r, g, b, 1.0f);
		bufferBuilder
				.addVertex(matrix4f, (float) (x2 - cameraPos.x), (float) (y2 - cameraPos.y), (float) (z2 - cameraPos.z))
				.setColor(r, g, b, 1.0f);
	}
	
	public static void drawLine(PoseStack matrixStack, Camera camera, double x1, double y1, double z1, double x2,
			double y2, double z2, Color color) {
		MultiBufferSource.BufferSource vertexConsumerProvider = mc.renderBuffers().bufferSource();
		RenderType layer = RenderTypes.debugQuads();
		VertexConsumer bufferBuilder = vertexConsumerProvider.getBuffer(layer);
		buildLine(matrixStack, camera, bufferBuilder, x1, y1, z1, x2, y2, z2, color);
		vertexConsumerProvider.endBatch(layer);
	}
	
	public static void drawFilledBox(PoseStack poseStack, AABB box, Color color) {
	    MultiBufferSource.BufferSource provider = mc.renderBuffers().bufferSource();
	    VertexConsumer vc = provider.getBuffer(KryosRenderTypes.filled());

	    float r = color.getRed() / 255f;
	    float g = color.getGreen() / 255f;
	    float b = color.getBlue() / 255f;
	    float a = color.getAlpha() / 255f;

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

	    provider.endBatch(KryosRenderTypes.filled());
	}
}