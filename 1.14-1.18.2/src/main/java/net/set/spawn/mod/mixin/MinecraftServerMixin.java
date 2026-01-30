package net.set.spawn.mod.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.server.MinecraftServer;
import net.set.spawn.mod.SeedHolder;
import net.set.spawn.mod.interfaces.MinecraftServerExtended;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;

@Mixin(MinecraftServer.class)
public abstract class MinecraftServerMixin implements MinecraftServerExtended {
    @Unique
    private final SeedHolder seedHolder = new SeedHolder();

    @Override
    public SeedHolder setspawnmod$getSeedHolder() {
        return seedHolder;
    }

    @Group(min = 1, max = 1)
    @ModifyExpressionValue(method = "createWorlds", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/ServerWorldProperties;isInitialized()Z"), require = 0)
    private boolean checkIfNewWorld(boolean initialized) {
        seedHolder.setNewWorld(!initialized);
        return initialized;
    }

    @Dynamic
    @Group
    // method_3786 -> createWorlds
    // LevelProperties#isInitialized
    @ModifyExpressionValue(method = "method_3786(Lnet/minecraft/class_29;Lnet/minecraft/class_31;Lnet/minecraft/class_1940;Lnet/minecraft/class_3949;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/class_31;method_222()Z"), require = 0, remap = false)
    private boolean checkIfNewWorld2(boolean initialized) {
        seedHolder.setNewWorld(!initialized);
        return initialized;
    }
}
