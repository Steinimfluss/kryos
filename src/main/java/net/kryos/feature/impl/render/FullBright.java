package net.kryos.feature.impl.render;

import net.kryos.Kryos;
import net.kryos.event.impl.PlayerTickEvent;
import net.kryos.event.listener.impl.PlayerTickListener;
import net.kryos.feature.Feature;
import net.kryos.feature.FeatureCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;

public class FullBright extends Feature implements PlayerTickListener {
    public FullBright() {
        super("FullBright", FeatureCategory.RENDER);
    }

    @Override
    protected void onEnable() {
        Kryos.eventBus.subscribe(this);
    }

    @Override
    protected void onDisable() {
        Kryos.eventBus.unsubscribe(this);
        mc.player.removeEffect(MobEffects.NIGHT_VISION);
    }

    @Override
    public void onPost(PlayerTickEvent.Post event) {
    	
    }

    @Override
    public void onPre(PlayerTickEvent.Pre event) {
        mc.player.addEffect(
            new MobEffectInstance(
                MobEffects.NIGHT_VISION,
                -1,
                1,
                false,
                false
            )
        );
    }
}
