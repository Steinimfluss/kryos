package net.kryos.feature;

import java.util.ArrayList;
import java.util.List;

public class FeatureManager {
    private final List<Feature> features = new ArrayList<>();

    public FeatureManager() {
        loadFeatures();
    }

    private void loadFeatures() {
    	
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
}
