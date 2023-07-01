package net.set.spawn.mod.mixin;

import net.minecraft.server.MinecraftServer;
import net.set.spawn.mod.SetSpawn;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftServer.class)
public abstract class MinecraftServerMixin {
    // method_20317 = prepareWorlds
    // side note: this method is so scuffed in 1.13, they changed it to have percentages on screen but didn't at the chunkmap yet,
    // and the implementation is very...interesting, glad they changed it in 1.14
    @Inject(method = "method_20317", at = @At(value = "HEAD"))
    public void startedWorldGen(CallbackInfo ci) {
        SetSpawn.shouldModifySpawn = true;
    }
}

