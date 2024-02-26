package net.set.spawn.mod.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.world.level.LevelInfo;
import net.set.spawn.mod.SetSpawn;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public abstract class MinecraftServerMixin {
    @Inject(method = "startIntegratedServer", at = @At("HEAD"))
    public void setspawnmod_startedWorldGen(String name, String displayName, LevelInfo levelInfo, CallbackInfo ci) {
        if (levelInfo != null) {
            SetSpawn.shouldModifySpawn = true;
        }
    }
}
