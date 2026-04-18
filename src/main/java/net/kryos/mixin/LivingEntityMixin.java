package net.kryos.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.kryos.Kryos;
import net.kryos.rotation.MovementCorrection;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {

    private float oldYRot$TRAVEL;
    private float oldYRot$JUMP;

    private static Minecraft getClient() {
        return Minecraft.getInstance();
    }
    
    private static MovementCorrection getMovementCorrection() {
    	return MovementCorrection.getFromString(Kryos.featureManager.rotationFeature.movementCorrection.getValue());
    }

    @Inject(method = "travel", at = @At("HEAD"))
    private void kryos$HEAD(Vec3 input, CallbackInfo ci) {
        Minecraft mc = getClient();
        if (mc != null && mc.player != null && (Object) this == mc.player && (getMovementCorrection() == MovementCorrection.STRICT || getMovementCorrection() == MovementCorrection.SILENT) && Kryos.rotationBus.rotating) {
        	oldYRot$TRAVEL = mc.player.getYRot();
            mc.player.setYRot(Kryos.rotationBus.yaw);
        }
    }

    @Inject(method = "travel", at = @At("TAIL"))
    private void kryos$TAIL(Vec3 input, CallbackInfo ci) {
        Minecraft mc = getClient();
        if (mc != null && mc.player != null && (Object) this == mc.player && (getMovementCorrection() == MovementCorrection.STRICT || getMovementCorrection() == MovementCorrection.SILENT) && Kryos.rotationBus.rotating) {
            mc.player.setYRot(oldYRot$TRAVEL);
        }
    }

    @Inject(method = "jumpFromGround", at = @At("HEAD"))
	public void jumpFromGround$HEAD(CallbackInfo ci) {
		Minecraft mc = getClient();
    	if(mc != null && mc.player != null && (Object) this == mc.player && (getMovementCorrection() == MovementCorrection.STRICT || getMovementCorrection() == MovementCorrection.SILENT) && Kryos.rotationBus.rotating) {
			oldYRot$JUMP = mc.player.getYRot();
	        mc.player.setYRot(Kryos.rotationBus.yaw);
    	}
	}

    @Inject(method = "jumpFromGround", at = @At("TAIL"))
	public void jumpFromGround$TAIL(CallbackInfo ci) {
		Minecraft mc = getClient();
    	if(mc != null && mc.player != null && (Object) this == mc.player && (getMovementCorrection() == MovementCorrection.STRICT || getMovementCorrection() == MovementCorrection.SILENT) && Kryos.rotationBus.rotating) {
    		mc.player.setYRot(oldYRot$JUMP);
    	}
	}
}
