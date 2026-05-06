package net.kryos.feature;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import net.kryos.event.impl.input.KeyPressEvent;
import net.kryos.event.listener.impl.input.KeyPressListener;
import net.kryos.feature.impl.misc.FakePlayer;
import net.kryos.feature.impl.render.ClickGui;
import net.kryos.feature.impl.render.FeatureList;
import net.kryos.feature.impl.world.PacketMine;
import net.minecraft.client.Minecraft;

public class FeatureManager implements KeyPressListener {
	private static final Minecraft mc = Minecraft.getInstance();
    private final List<Feature> features = new ArrayList<>();
    
    public ClickGui clickGui = register(new ClickGui());
    public FeatureList featureList = register(new FeatureList());
    public FakePlayer fakePlayer = register(new FakePlayer());
    public PacketMine packetMine = register(new PacketMine());
    
    private <T extends Feature> T register(T feature) {
        features.add(feature);
        return feature;
    }

    public List<Feature> getFeatures() {
        return features;
    }
    
    public Optional<Feature> getFeaturesById(String id) {
        for (Feature f : features) {
            if (f.getId().contentEquals(id)) {
                return Optional.of(f);
            }
        }
        
        return Optional.empty();
    }

    public List<Feature> getFeaturesByCategory(FeatureCategory category) {
        List<Feature> list = new ArrayList<>();
        for (Feature f : features) {
            if (f.getCategory() == category) {
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
				if(feature.getKey().orElseGet(() -> -1) == event.getEvent().input()) {
					feature.toggle();
				}
			});
		}
	}
}