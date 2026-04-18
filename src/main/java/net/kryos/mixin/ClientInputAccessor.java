package net.kryos.mixin;

import net.minecraft.client.player.ClientInput;
import net.minecraft.world.entity.player.Input;
import net.minecraft.world.phys.Vec2;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ClientInput.class)
public interface ClientInputAccessor {
    @Accessor("keyPresses")
    Input kryos$getKeyPresses();

    @Accessor("keyPresses")
    void kryos$setKeyPresses(Input input);

    @Accessor("moveVector")
    Vec2 kryos$getMoveVector();

    @Accessor("moveVector")
    void kryos$setMoveVector(Vec2 vec);
}
