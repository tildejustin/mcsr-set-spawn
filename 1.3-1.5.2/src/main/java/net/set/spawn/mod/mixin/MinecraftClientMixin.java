package net.set.spawn.mod.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import net.minecraft.client.Minecraft;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.world.level.LevelInfo;
import net.set.spawn.mod.interfaces.MinecraftServerExtended;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public abstract class MinecraftClientMixin {
    @Inject(method = "method_2935", at = @At("HEAD"))
    private void markIfNewWorld(String worldName, String levelName, LevelInfo levelInfo, CallbackInfo ci, @Share("newWorld") LocalBooleanRef newWorld) {
        newWorld.set(levelInfo != null);
    }

    @ModifyExpressionValue(method = "method_2935", at = @At(value = "NEW", target = "net/minecraft/server/integrated/IntegratedServer"))
    private IntegratedServer createIntegratedServer(IntegratedServer original, @Share("newWorld") LocalBooleanRef newWorld) {
        ((MinecraftServerExtended) original).setspawnmod$setShouldModifySpawn(newWorld.get());
        return original;
    }
}
