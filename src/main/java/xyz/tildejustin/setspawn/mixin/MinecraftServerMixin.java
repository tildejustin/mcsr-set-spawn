package xyz.tildejustin.setspawn.mixin;

import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.tildejustin.setspawn.SetSpawn;

@Mixin(MinecraftServer.class)
public abstract class MinecraftServerMixin {

    @Inject(method = "prepareWorlds", at = @At(value = "HEAD"))
    public void setspawnmod_startedWorldGen(CallbackInfo ci) {
        SetSpawn.ServerPlayerEntityInitCounter = 0;
    }
}

