package net.set.spawn.mod.seed.mixin;

import net.minecraft.world.DemoServerWorld;
import net.set.spawn.mod.interfaces.SetSeedHolder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DemoServerWorld.class)
public abstract class DemoServerWorldMixin {
    @Inject(method = "<clinit>", at = @At("TAIL"))
    private static void setDemoSetSeed(CallbackInfo ci) {
        ((SetSeedHolder) (Object) DemoServerWorld.INFO).setspawnmod$setSetSeed(true);
    }
}
