package net.kryos.gui.component.impl.config;

import org.lwjgl.glfw.GLFW;

import net.kryos.gui.MainTheme;
import net.kryos.gui.component.Component;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;

public class ConfigTextInput extends Component {
    private static final int WIDTH = 100;
    private static final int HEIGHT = 15;

    private boolean selected;
    public final StringBuilder text = new StringBuilder();

    public ConfigTextInput() {
        this.width = WIDTH;
        this.height = HEIGHT;
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
    	graphics.enableScissor(x, y, x + width, y + height);
        graphics.fill(x, y, x + width, y + height, selected ? MainTheme.SECONDARY : MainTheme.TERTIARY);
        graphics.outline(x, y, width, height, -1);

        int delta = font.width(text.toString()) - width;
        
        int relX = delta > 0 ? (x - delta - 2) : (x + 2);
        graphics.text(
                font,
                text.toString(),
                relX,
                y + height / 2 - font.lineHeight / 2,
                -1
        );
        
        if(selected)
        	graphics.verticalLine(relX + font.width(text.toString()), y + 2, y + HEIGHT - 2, -1);
        
        graphics.disableScissor();
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        if (isHovered(event.x(), event.y())) {
            if (event.button() == 0)
                selected = true;
            return true;
        } else {
            selected = false;
        }

        return super.mouseClicked(event, doubleClick);
    }

    @Override
    public void keyPressed(KeyEvent event) {
        if (!selected)
            return;

        if (event.key() == GLFW.GLFW_KEY_BACKSPACE) {
            if (event.hasControlDown()) {
                text.setLength(0);
            } else if (text.length() > 0) {
                text.deleteCharAt(text.length() - 1);
            }
            return;
        }
    }

    @Override
    public void charTyped(CharacterEvent event) {
        if (!selected)
            return;
        
        char character = (char) event.codepoint();

        if (Character.isLetter(character) || Character.isDigit(character)) {
            text.append(character);
        }
    }

    public String getText() {
        return text.toString();
    }
}
