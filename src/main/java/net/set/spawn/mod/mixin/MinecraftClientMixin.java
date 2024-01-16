package net.set.spawn.mod.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.world.level.LevelInfo;
import net.set.spawn.mod.SetSpawn;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {
    @Inject(method = "startIntegratedServer", at = @At("HEAD"))
    private void setShouldModify(String worldName, String levelName, LevelInfo levelInfo, CallbackInfo ci) {
        if (levelInfo != null) {
            SetSpawn.shouldModifySpawn = true;
        }
    }
}
