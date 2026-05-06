package net.kryos.event.impl.render;

import org.joml.Matrix4fc;
import org.joml.Vector4f;

import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.resource.GraphicsResourceAllocator;

import net.kryos.event.Event;
import net.kryos.event.listener.impl.render.ExtractLevelListener;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.renderer.chunk.ChunkSectionsToRender;
import net.minecraft.client.renderer.state.level.CameraRenderState;

public class ExtractLevelEvent extends Event<ExtractLevelListener> {
	private final GraphicsResourceAllocator resourceAllocator;
	private final DeltaTracker deltaTracker;
	private final boolean renderOutline;
	private final CameraRenderState cameraState;
	private final Matrix4fc modelViewMatrix;
	private final GpuBufferSlice terrainFog;
	private final Vector4f fogColor;
	private final boolean shouldRenderSky;
	private final ChunkSectionsToRender chunkSectionsToRender;
	
	public ExtractLevelEvent(GraphicsResourceAllocator resourceAllocator, DeltaTracker deltaTracker,
			boolean renderOutline, CameraRenderState cameraState, Matrix4fc modelViewMatrix, GpuBufferSlice terrainFog,
			Vector4f fogColor, boolean shouldRenderSky, ChunkSectionsToRender chunkSectionsToRender) {
		this.resourceAllocator = resourceAllocator;
		this.deltaTracker = deltaTracker;
		this.renderOutline = renderOutline;
		this.cameraState = cameraState;
		this.modelViewMatrix = modelViewMatrix;
		this.terrainFog = terrainFog;
		this.fogColor = fogColor;
		this.shouldRenderSky = shouldRenderSky;
		this.chunkSectionsToRender = chunkSectionsToRender;
	}

	public GraphicsResourceAllocator getResourceAllocator() {
		return resourceAllocator;
	}

	public DeltaTracker getDeltaTracker() {
		return deltaTracker;
	}

	public boolean isRenderOutline() {
		return renderOutline;
	}

	public CameraRenderState getCameraState() {
		return cameraState;
	}

	public Matrix4fc getModelViewMatrix() {
		return modelViewMatrix;
	}

	public GpuBufferSlice getTerrainFog() {
		return terrainFog;
	}

	public Vector4f getFogColor() {
		return fogColor;
	}

	public boolean isShouldRenderSky() {
		return shouldRenderSky;
	}

	public ChunkSectionsToRender getChunkSectionsToRender() {
		return chunkSectionsToRender;
	}

	@Override
	public void post(ExtractLevelListener listener) {
		listener.extractLevel(this);
	}

	@Override
	public Class<ExtractLevelListener> getListenerType() {
		return ExtractLevelListener.class;
	}
}