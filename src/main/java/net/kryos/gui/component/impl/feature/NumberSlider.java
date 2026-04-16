package net.kryos.gui.component.impl.feature;

import net.kryos.feature.setting.NumberSetting;
import net.kryos.gui.MainTheme;
import net.kryos.gui.component.Component;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.input.MouseButtonEvent;

public class NumberSlider extends Component {
    private static final int WIDTH = 100;
    private static final int HEIGHT = 15;

    private final NumberSetting<?> setting;
    private boolean dragging = false;

    public NumberSlider(NumberSetting<?> setting) {
        this.setting = setting;
        this.width = WIDTH;
        this.height = HEIGHT;
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        if (dragging) {
            double percent = (mouseX - x - 8) / (double) (width - 16);
            percent = Math.max(0, Math.min(1, percent));
            double newValue = setting.min.doubleValue() + percent * (setting.max.doubleValue() - setting.min.doubleValue());
            setting.setValueFromDouble(newValue);
        }

        int vx1 = x + 8;
        int vx2 = x + width - 8;

        graphics.fill(vx1, y, vx2, y + height, MainTheme.TERTIARY);

        double range = setting.max.doubleValue() - setting.min.doubleValue();
        double percent = (setting.getValue().doubleValue() - setting.min.doubleValue()) / range;

        int fillWidth = (int) (percent * (width - 16));
        graphics.fill(vx1, y, vx1 + fillWidth, y + height, MainTheme.SELECTED);
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        if (isHovered(event.x(), event.y())) {
            dragging = true;
            return true;
        }
        return false;
    }

    @Override
    public void mouseReleased(MouseButtonEvent event) {
        dragging = false;
    }
}
