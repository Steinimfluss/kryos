package net.kryos.gui.click.impl;

import net.kryos.Kryos;
import net.kryos.feature.Feature;
import net.kryos.feature.FeatureCategory;
import net.kryos.gui.widget.Widget;

public class FeaturesWidget extends Widget {
    private static final int CATEGORY_WIDTH = 150;
    private static final int CATEGORY_HEIGHT = 20;
    private static final int PADDING_X = 10;
    private static final int PADDING_Y = 10;

    public FeaturesWidget() {
        rebuildLayout();
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        rebuildLayout();
    }

    private void rebuildLayout() {
        this.children.clear();

        int guiWidth = mc.getWindow().getGuiScaledWidth();

        int x = PADDING_X;
        int y = PADDING_Y;

        for (FeatureCategory category : FeatureCategory.values()) {
            if (x + CATEGORY_WIDTH > guiWidth) {
                x = PADDING_X;
                y += CATEGORY_HEIGHT + PADDING_Y;
            }

            createCategory(category, x, y);

            x += CATEGORY_WIDTH + PADDING_X;
        }
    }

    private void createCategory(FeatureCategory category, int x, int y) {
        CategoryWidget list = new CategoryWidget(category);
        list.x = x;
        list.y = y;

        for (Feature feature : Kryos.featureManager.getFeaturesByCategory(category)) {
            FeatureWidget button = new FeatureWidget(feature);
            list.add(button);
        }

        this.add(list);
    }
}
