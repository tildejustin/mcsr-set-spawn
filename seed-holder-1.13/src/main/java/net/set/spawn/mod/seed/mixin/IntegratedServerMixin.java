package net.set.spawn.mod.seed.mixin;

import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.world.level.LevelInfo;
import net.set.spawn.mod.interfaces.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(IntegratedServer.class)
public abstract class IntegratedServerMixin implements MinecraftServerExtended {
    @Shadow
    @Final
    private LevelInfo levelInfo;

    @Inject(method = "<init>*", at = @At("TAIL"))
    private void setSetSeed(CallbackInfo ci) {
        this.setspawnmod$getSeedHolder().setSetSeed(((SetSeedHolder) (Object) this.levelInfo).setspawnmod$isSetSeed());
    }
}
