package net.set.spawn.mod.seed.mixin;

import net.minecraft.world.level.LevelInfo;
import net.set.spawn.mod.interfaces.SetSeedHolder;
import org.spongepowered.asm.mixin.*;

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
        System.out.println("holder set seed: " + setSeed);
        this.setSeed = setSeed;
    }
}
