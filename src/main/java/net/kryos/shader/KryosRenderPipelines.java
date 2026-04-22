package net.kryos.shader;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.ColorTargetState;
import com.mojang.blaze3d.pipeline.DepthStencilState;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.CompareOp;
import com.mojang.blaze3d.shaders.UniformType;
import com.mojang.blaze3d.systems.GpuDevice;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;

import net.kryos.Kryos;
import net.minecraft.client.Minecraft;
import net.minecraft.server.packs.resources.ResourceManager;

public class KryosRenderPipelines {
	private static final List<RenderPipeline> PIPELINES = new ArrayList<>();
	
	public static final RenderPipeline.Snippet MATRICES_PROJECTION_SNIPPET = RenderPipeline.builder()
			.withUniform("DynamicTransforms", UniformType.UNIFORM_BUFFER)
			.withUniform("Projection", UniformType.UNIFORM_BUFFER)
			.buildSnippet();
	
	public static final RenderPipeline.Snippet DEBUG_FILLED_SNIPPET = RenderPipeline.builder(MATRICES_PROJECTION_SNIPPET)
			.withVertexShader("core/position_color")
			.withFragmentShader("core/position_color")
			.withColorTargetState(new ColorTargetState(BlendFunction.TRANSLUCENT))
			.withVertexFormat(DefaultVertexFormat.POSITION_COLOR, VertexFormat.Mode.QUADS)
			.withDepthStencilState(new DepthStencilState(CompareOp.ALWAYS_PASS, false))
			.buildSnippet();
	
	public static final RenderPipeline DEBUG_QUADS = register(
			RenderPipeline.builder(DEBUG_FILLED_SNIPPET).withLocation(Kryos.id("pipeline/debug_quads")).withCull(false).build()
		);
	
	private static RenderPipeline register(RenderPipeline pipeline) {
        PIPELINES.add(pipeline);
        return pipeline;
    }

	public static void precompile() {
	    GpuDevice device = RenderSystem.getDevice();
	    ResourceManager resources = Minecraft.getInstance().getResourceManager();

	    for (RenderPipeline pipeline : PIPELINES) {
	        device.precompilePipeline(pipeline, (identifier, _) -> {
	            var resource = resources.getResource(identifier).get();

	            try (InputStream in = resource.open()) {
	                return new String(in.readAllBytes(), StandardCharsets.UTF_8);
	            } catch (IOException e) {
	                throw new RuntimeException(e);
	            }
	        });
	    }
	}
}
