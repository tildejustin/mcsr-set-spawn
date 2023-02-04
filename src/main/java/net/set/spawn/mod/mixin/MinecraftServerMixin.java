package net.set.spawn.mod.mixin;

import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.set.spawn.mod.SetSpawn;

@Mixin(MinecraftServer.class)
public abstract class MinecraftServerMixin {

    @Inject(method = "prepareWorlds", at = @At(value = "HEAD"))
    public void setspawnmod_startedWorldGen(CallbackInfo ci) {
        // reset count on new world
        SetSpawn.ServerPlayerEntityInitCounter = 0;
    }
}

