package net.kryos.feature.impl.combat;

import net.kryos.Kryos;
import net.kryos.event.impl.AttackEvent;
import net.kryos.event.listener.impl.AttackListener;
import net.kryos.feature.Feature;
import net.kryos.feature.FeatureCategory;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;
import net.minecraft.world.phys.Vec3;

public class Criticals extends Feature implements AttackListener {
	public Criticals() {
		super("Criticals", FeatureCategory.COMBAT);
	}

	@Override
	protected void onEnable() {
		Kryos.eventBus.subscribe(this);
	}

	@Override
	protected void onDisable() {
		Kryos.eventBus.unsubscribe(this);
	}

	@Override
	public void attack(AttackEvent event) {
		if(!(mc.player.fallDistance > 0)
				&& mc.player.onGround() 
				&& !mc.player.onClimbable()
				&& !mc.player.isInWater()
				&& !mc.player.isMobilityRestricted()
				&& !mc.player.isPassenger()
				&& !mc.player.isSprinting()) {
			sendPos(mc.player.position().add(0, 0.0001, 0));
			sendPos(mc.player.position());
		}
	}
	
	public void sendPos(Vec3 pos) {
		mc.getConnection().send(new ServerboundMovePlayerPacket.Pos(pos, false, mc.player.horizontalCollision));
	}
}