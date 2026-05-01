package net.kryos.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import net.kryos.Kryos;
import net.kryos.rotation.MovementCorrection;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.player.KeyboardInput;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Input;

@Mixin(KeyboardInput.class)
public abstract class KeyboardInputMixin {

    @Shadow @Final private Options options;

    @Shadow
    private static float calculateImpulse(boolean positive, boolean negative) {
        throw new AssertionError();
    }

    @Overwrite
    public void tick() {
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;

        ClientInputAccessor acc = (ClientInputAccessor) (Object) this;

        boolean up    = this.options.keyUp.isDown();
        boolean down  = this.options.keyDown.isDown();
        boolean left  = this.options.keyLeft.isDown();
        boolean right = this.options.keyRight.isDown();

        if (Kryos.rotationBus.lastRotating &&
            MovementCorrection.getFromString(Kryos.featureManager.rotationFeature.movementCorrection.getValue().getName()) == MovementCorrection.SILENT &&
            player != null) {

            float clientYaw = player.getYRot();
            float serverYaw = Kryos.rotationBus.yaw;
            float delta = serverYaw - clientYaw;

            float rad = (float) Math.toRadians(delta);
            float cos = Mth.cos(rad);
            float sin = Mth.sin(rad);

            float vx = (right ? 1 : 0) - (left ? 1 : 0);
            float vy = (up ? 1 : 0) - (down ? 1 : 0);

            float rx = vx * cos - vy * sin;
            float ry = vx * sin + vy * cos;

            up    = ry > 0.25f;
            down  = ry < -0.25f;
            left  = rx < -0.25f;
            right = rx > 0.25f;
        }

        Input presses = new Input(
            up,
            down,
            left,
            right,
            this.options.keyJump.isDown(),
            this.options.keyShift.isDown(),
            this.options.keySprint.isDown()
        );
        acc.kryos$setKeyPresses(presses);

        float forwardImpulse = calculateImpulse(presses.forward(), presses.backward());
        float leftImpulse = calculateImpulse(presses.left(), presses.right());

        acc.kryos$setMoveVector(new net.minecraft.world.phys.Vec2(leftImpulse, forwardImpulse).normalized());
    }
}
