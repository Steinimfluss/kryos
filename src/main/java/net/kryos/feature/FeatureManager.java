package net.kryos.feature;

import java.util.ArrayList;
import java.util.List;

import net.kryos.event.impl.KeyPressEvent;
import net.kryos.event.listener.impl.KeyPressListener;
import net.kryos.feature.impl.combat.Offhand;
import net.kryos.feature.impl.combat.Velocity;
import net.kryos.feature.impl.combat.Killaura;
import net.kryos.feature.impl.misc.RotationFeature;
import net.kryos.feature.impl.render.ClickGui;
import net.kryos.feature.impl.render.FeatureList;
import net.kryos.feature.impl.render.Fog;
import net.kryos.feature.impl.render.NoRender;
import net.kryos.feature.impl.render.ViewModel;
import net.minecraft.client.Minecraft;

public class FeatureManager implements KeyPressListener {
	private static final Minecraft mc = Minecraft.getInstance();
    private final List<Feature> features = new ArrayList<>();
    
    public ClickGui clickGui = new ClickGui();
    public FeatureList featureList = new FeatureList();
    public Fog fog = new Fog();
    public ViewModel viewModel = new ViewModel();
    public NoRender noRender = new NoRender();
    public Killaura killaura = new Killaura();
    public RotationFeature rotationFeature = new RotationFeature();
    public Offhand offhand = new Offhand();
    public Velocity velocity = new Velocity();
    
    public FeatureManager() {
        loadFeatures();
    }

    private void loadFeatures() {
    	features.add(clickGui);
    	features.add(featureList);
    	features.add(fog);
    	features.add(viewModel);
    	features.add(noRender);
    	features.add(killaura);
    	features.add(rotationFeature);
    	features.add(offhand);
    	features.add(velocity);
    }

    public List<Feature> getFeatures() {
        return features;
    }

    public Feature getFeatureByName(String name) {
        for (Feature f : features) {
            if (f.name.equalsIgnoreCase(name)) {
                return f;
            }
        }
        return null;
    }

    public List<Feature> getFeaturesByCategory(FeatureCategory category) {
        List<Feature> list = new ArrayList<>();
        for (Feature f : features) {
            if (f.category == category) {
                list.add(f);
            }
        }
        return list;
    }

	@Override
	public void onKeyPress(KeyPressEvent event) {
		if(mc.screen != null) return;
		
		if(event.getAction() == 1) {
			features.forEach(feature -> {
				if(feature.getKey() == event.getEvent().input()) {
					feature.toggle();
				}
			});
		}
	}
}