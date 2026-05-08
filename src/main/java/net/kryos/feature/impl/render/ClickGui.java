package net.kryos.feature.impl.render;

import java.util.Optional;

import org.lwjgl.glfw.GLFW;

import net.kryos.feature.Feature;
import net.kryos.feature.FeatureCategory;
import net.minecraft.network.chat.Component;

public class ClickGui extends Feature {
	public net.kryos.gui.click.ClickGuiScreen clickGui;

	public ClickGui() {
		super("click_gui", "ClickGui", FeatureCategory.RENDER, Component.literal("An interface for indexing different features"));
		setKey(Optional.of(GLFW.GLFW_KEY_RIGHT_SHIFT));
	}
	
	@Override
	protected void onEnable() {
		if(clickGui == null)
			clickGui = new net.kryos.gui.click.ClickGuiScreen();
		
		mc.setScreen(clickGui);
		setEnabled(false);
	}
}