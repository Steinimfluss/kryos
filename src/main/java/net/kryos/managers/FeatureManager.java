package net.kryos.managers;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import net.kryos.event.impl.input.KeyPressEvent;
import net.kryos.event.listener.impl.input.KeyPressListener;
import net.kryos.feature.Feature;
import net.kryos.feature.FeatureCategory;
import net.kryos.feature.impl.combat.CrystalAura;
import net.kryos.feature.impl.combat.Offhand;
import net.kryos.feature.impl.combat.Velocity;
import net.kryos.feature.impl.misc.FakePlayer;
import net.kryos.feature.impl.movement.Speed;
import net.kryos.feature.impl.movement.Step;
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
    public CrystalAura crystalAura = register(new CrystalAura());
    public Offhand offhand = register(new Offhand());
    public Velocity velocity = register(new Velocity());
    public Speed speed = register(new Speed());
    public Step step = register(new Step());
    
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