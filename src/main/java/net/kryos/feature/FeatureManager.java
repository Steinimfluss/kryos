package net.kryos.feature;

import java.util.ArrayList;
import java.util.List;

import net.kryos.event.impl.KeyPressEvent;
import net.kryos.event.listener.impl.KeyPressListener;
import net.kryos.feature.impl.render.ClickGui;
import net.kryos.feature.impl.render.FeatureList;
import net.kryos.feature.impl.render.Fog;
import net.kryos.feature.impl.render.NoRender;
import net.kryos.feature.impl.render.ViewModel;

public class FeatureManager implements KeyPressListener {
    private final List<Feature> features = new ArrayList<>();
    
    public FeatureManager() {
        loadFeatures();
    }

    private void loadFeatures() {
    	features.add(new ClickGui());
    	features.add(new FeatureList());
    	features.add(new Fog());
    	features.add(new ViewModel());
    	features.add(new NoRender());
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
		if(event.getAction() == 1) {
			features.forEach(feature -> {
				if(feature.getKey() == event.getEvent().input()) {
					feature.toggle();
				}
			});
		}
	}
}