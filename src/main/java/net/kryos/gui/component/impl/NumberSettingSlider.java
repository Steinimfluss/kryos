package net.kryos.gui.component.impl;

import com.mojang.blaze3d.platform.cursor.CursorTypes;

import net.kryos.feature.setting.NumberSetting;
import net.kryos.gui.MainTheme;
import net.kryos.gui.component.Component;
import net.kryos.render.KryosGraphicsExtractor;
import net.minecraft.client.input.MouseButtonEvent;

public class NumberSettingSlider extends SettingComponent {
    private static final int HEIGHT = 3;
    
    private final NumberSetting<?> setting;

    private boolean dragging = false;

    public NumberSettingSlider(Component parent, NumberSetting<?> setting, float width) {
    	super(parent);
        this.width = width;
        this.height = HEIGHT;
        
        this.setting = setting;
    }

    @Override
    public void extractRenderState(KryosGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
    	if(dragging) {
			graphics.requestCursor(CursorTypes.RESIZE_EW);
    	} else if(isHovered(mouseX, mouseY)) {
			graphics.requestCursor(CursorTypes.POINTING_HAND);
		}
		
        if (dragging) {
            double percent = (mouseX - getX()) / (double) (width);
            percent = Math.max(0, Math.min(1, percent));
            double newValue = setting.getMin().doubleValue() + percent * (setting.getMax().doubleValue() - setting.getMin().doubleValue());
            setting.setValueFromDouble(newValue);
        }

        float vx1 = getX();
        float vx2 = getX() + width;

        graphics.fill(vx1, getY(), vx2, getY() + height, MainTheme.TERTIARY);

        double range = setting.getMax().doubleValue() - setting.getMin().doubleValue();
        double percent = (setting.getValue().doubleValue() - setting.getMin().doubleValue()) / range;

        int fillWidth = (int) (percent * width);
        graphics.fill(vx1, getY(), vx1 + width, getY() + height, MainTheme.LINE);
        graphics.fill(vx1, getY(), vx1 + fillWidth, getY() + height, MainTheme.SELECTED);
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
    	if(super.mouseClicked(event, doubleClick)) return true;
    	
        if (isHovered(event.x(), event.y())) {
            dragging = true;
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseReleased(MouseButtonEvent event) {
    	if(super.mouseReleased(event)) return true;

    	if(dragging) {
            dragging = false;
            return true;
    	}
    	return false;
    }
    
    @Override
    public boolean mouseScrolled(double x, double y, double scrollX, double scrollY) {
    	if(super.mouseScrolled(x, y, scrollX, scrollY)) return true;
    	
    	if(isHovered(x, y)) {
    		setting.setValueFromDouble(setting.getValue().doubleValue() + (setting.getStep().doubleValue() * (scrollY / Math.abs(scrollY))));
    		return true;
    	}
    	
    	return false;
    }
}