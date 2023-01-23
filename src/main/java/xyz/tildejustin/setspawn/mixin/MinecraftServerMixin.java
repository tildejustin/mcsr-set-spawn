package xyz.tildejustin.setspawn.mixin;

import net.minecraft.server.MinecraftServer;
import xyz.tildejustin.setspawn.SetSpawn;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftServer.class)
public abstract class MinecraftServerMixin {

    @Inject(method = "prepareWorlds", at = @At(value = "HEAD"))
    public void setspawnmod_startedWorldGen(CallbackInfo ci){
        if (SetSpawn.config.isEnabled()) {
            SetSpawn.shouldModifySpawn = true;
        }
    }
}

