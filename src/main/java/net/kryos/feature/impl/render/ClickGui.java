package net.kryos.feature.impl.render;

import java.util.List;

import org.lwjgl.glfw.GLFW;

import net.kryos.feature.Feature;
import net.kryos.feature.FeatureCategory;
import net.kryos.feature.Keybind;
import net.kryos.gui.component.impl.CategoryPanel;

public class ClickGui extends Feature {
	public net.kryos.gui.ClickGui clickGui;
	
	public ClickGui() {
		super("ClickGUI", FeatureCategory.RENDER);
		setKey(new Keybind(GLFW.GLFW_KEY_RIGHT_SHIFT, 54));
	}

	@Override
	protected void onEnable() {
		if(clickGui == null)
			clickGui = new net.kryos.gui.ClickGui();
		
		mc.setScreen(clickGui);
		setEnabled(false);
	}

	@Override
	protected void onDisable() {
		
	}
    
    public List<CategoryPanel> getPanels() {
        return clickGui.getPanels();
    }

    public CategoryPanel getPanelByCategory(FeatureCategory c) {
    	return clickGui.getPanelByCategory(c);
    }
}