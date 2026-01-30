package net.set.spawn.mod.seed.mixin;

import net.minecraft.server.MinecraftServer;
import net.set.spawn.mod.interfaces.SetSeedHolder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftServer.class)
public abstract class MinecraftServerMixin {
    @Inject(method = "<clinit>", at = @At("TAIL"))
    private static void setDemoSetSeed(CallbackInfo ci) {
        ((SetSeedHolder) (Object) MinecraftServer.DEMO_LEVEL_INFO).setspawnmod$setSetSeed(true);
    }
}
