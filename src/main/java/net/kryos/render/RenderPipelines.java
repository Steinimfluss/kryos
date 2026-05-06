package net.kryos.render;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

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
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.ResourceManager;

public class RenderPipelines {
	private static final Map<Identifier, RenderPipeline> PIPELINES_BY_LOCATION = new HashMap<>();

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
			RenderPipeline.builder(DEBUG_FILLED_SNIPPET)
			.withLocation(Kryos.id("pipeline/debug_quads"))
			.withCull(false)
			.build());
	
	public static RenderPipeline register(final RenderPipeline pipeline) {
		PIPELINES_BY_LOCATION.put(pipeline.getLocation(), pipeline);
		return pipeline;
	}
	
	public static void precompile() {
        GpuDevice device = RenderSystem.getDevice();
        ResourceManager resources = Minecraft.getInstance().getResourceManager();

        for (RenderPipeline pipeline : PIPELINES_BY_LOCATION.values()) {
            device.precompilePipeline(pipeline, (id, _) -> {
                var resource = resources.getResource(id).orElseThrow();

                try (var in = resource.open()) {
                    return new String(in.readAllBytes(), StandardCharsets.UTF_8);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }
}