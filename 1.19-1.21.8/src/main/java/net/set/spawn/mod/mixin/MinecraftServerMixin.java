package net.set.spawn.mod.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.SaveProperties;
import net.set.spawn.mod.SeedHolder;
import net.set.spawn.mod.interfaces.MinecraftServerExtended;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(MinecraftServer.class)
public abstract class MinecraftServerMixin implements MinecraftServerExtended {
    @Unique
    private final SeedHolder seedHolder = new SeedHolder();

    @Override
    public SeedHolder setspawnmod$getSeedHolder() {
        return seedHolder;
    }

    @ModifyExpressionValue(method = "createWorlds", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/ServerWorldProperties;isInitialized()Z"), require = 0)
    private boolean checkIfNewWorld(boolean initialized) {
        this.setspawnmod$getSeedHolder().setNewWorld(!initialized);
        return initialized;
    }
}
