package net.kryos.gui.click.impl;

import net.kryos.gui.MainTheme;
import net.kryos.gui.widget.Widget;
import net.kryos.setting.impl.StringSetting;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import org.lwjgl.glfw.GLFW;

public class StringSettingWidget extends Widget {
    private static final int WIDTH = 146;
    private static final int HEIGHT = 20;

    private final StringSetting setting;
    private boolean listening = false;

    public StringSettingWidget(StringSetting setting) {
        this.setting = setting;
        this.width = WIDTH;
        this.height = HEIGHT;
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        if (!visible()) return;

        graphics.text(font, setting.getName(),
                x + 4,
                y + height / 2 - font.lineHeight / 2 + (isHovered(mouseX, mouseY) ? -1 : 0),
                MainTheme.TEXT.getRGB());

        String value = setting.getValue();
        if (listening) value += "_";

        graphics.text(font, value,
                x + width - font.width(value) - 4,
                y + height / 2 - font.lineHeight / 2,
                MainTheme.TEXT.getRGB());

        super.extractRenderState(graphics, mouseX, mouseY, a);
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        if (!visible()) return false;
        
        if (super.mouseClicked(event, doubleClick)) return true;

        if (isHovered(event.x(), event.y())) {
            if (event.button() == GLFW.GLFW_MOUSE_BUTTON_LEFT) listening = !listening;
            
			if(event.button() == GLFW.GLFW_MOUSE_BUTTON_MIDDLE) setting.reset();
			
            return true;
        } else {
            listening = false;
        }

        return false;
    }

    @Override
    public boolean charTyped(CharacterEvent event) {
        if (!visible() || !listening) return false;

        char c = (char) event.codepoint();

        if (c >= 32 && c != 127) {
            setting.setValue(setting.getValue() + c);
            return true;
        }

        return false;
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        if (!visible() || !listening) return false;

        int key = event.key();

        if (key == GLFW.GLFW_KEY_BACKSPACE) {
            String current = setting.getValue();
            if (!current.isEmpty()) {
                setting.setValue(current.substring(0, current.length() - 1));
            }
            return true;
        }

        if (key == GLFW.GLFW_KEY_ENTER || key == GLFW.GLFW_KEY_KP_ENTER) {
            listening = false;
            return true;
        }

        return false;
    }

    @Override
    public boolean visible() {
        return setting.visible();
    }
}