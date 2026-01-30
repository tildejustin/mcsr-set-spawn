package net.set.spawn.mod.seed.mixin;

import net.minecraft.class_3199;
import net.set.spawn.mod.interfaces.SetSeedHolder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(class_3199.class)
public abstract class DemoServerWorldMixin {
    @Inject(method = "<clinit>", at = @At("TAIL"))
    private static void setDemoSetSeed(CallbackInfo ci) {
        // field_13884 -> DemoServerWorld.INFO
        ((SetSeedHolder) (Object) class_3199.field_13884).setspawnmod$setSetSeed(true);
    }
}
