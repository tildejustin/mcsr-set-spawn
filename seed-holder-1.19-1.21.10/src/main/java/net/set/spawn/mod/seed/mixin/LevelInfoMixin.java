package net.set.spawn.mod.seed.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.world.level.LevelInfo;
import net.set.spawn.mod.interfaces.SetSeedHolder;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LevelInfo.class)
public abstract class LevelInfoMixin implements SetSeedHolder {
    @Unique
    private boolean setSeed = false;

    @Override
    public boolean setspawnmod$isSetSeed() {
        return setSeed;
    }

    @Override
    public void setspawnmod$setSetSeed(boolean setSeed) {
        System.out.println("li set seed: " + setSeed);
        this.setSeed = setSeed;
    }

    @ModifyExpressionValue(method = "*", at = @At(value = "NEW", target = "net/minecraft/world/level/LevelInfo"))
    private LevelInfo curryLevelInfo(LevelInfo original) {
        ((SetSeedHolder) (Object) original).setspawnmod$setSetSeed(this.setspawnmod$isSetSeed());
        return original;
    }
}
