package net.kryos.gui.click;

import net.kryos.Kryos;
import net.kryos.gui.MainTheme;
import net.kryos.gui.click.impl.FeaturesWidget;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;

public class ClickGuiScreen extends Screen {
    private final FeaturesWidget features;

    public ClickGuiScreen() {
        super(Component.literal("Click GUI"));
        
        features = new FeaturesWidget();
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
    	Kryos.tooltipManager.clearTooltip();
    	
    	features.extractRenderState(graphics, mouseX, mouseY, delta);
        super.extractRenderState(graphics, mouseX, mouseY, delta);
        
        Component tooltip = Kryos.tooltipManager.getTooltip();
        
        if(!tooltip.getString().isEmpty()) {
	        graphics.fill(mouseX + 6, mouseY + 6, mouseX + font.width(tooltip) + 8, mouseY + font.lineHeight + 8, MainTheme.PRIMARY.getRGB());
	    	graphics.text(font, tooltip, mouseX + 7, mouseY + 7, -1);
        }
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        if (features.mouseClicked(event, doubleClick)) return true;
        return super.mouseClicked(event, doubleClick);
    }

    @Override
    public boolean mouseReleased(MouseButtonEvent event) {
        if (features.mouseReleased(event)) return true;
        return super.mouseReleased(event);
    }

    @Override
    public boolean mouseDragged(MouseButtonEvent event, double dx, double dy) {
        if (features.mouseDragged(event, dx, dy)) return true;
        return super.mouseDragged(event, dx, dy);
    }

    @Override
    public boolean mouseScrolled(double x, double y, double scrollX, double scrollY) {
        if (features.mouseScrolled(x, y, scrollX, scrollY)) return true;
        return super.mouseScrolled(x, y, scrollX, scrollY);
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        if (features.keyPressed(event)) return true;
        return super.keyPressed(event);
    }

    @Override
    public boolean charTyped(CharacterEvent event) {
        if (features.charTyped(event)) return true;
        return super.charTyped(event);
    }
    
    @Override
    public void resize(int width, int height) {
    	features.resize(width, height);
    	super.resize(width, height);
    }
}