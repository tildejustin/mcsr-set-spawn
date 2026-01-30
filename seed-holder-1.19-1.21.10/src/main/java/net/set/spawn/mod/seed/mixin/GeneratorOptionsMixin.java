package net.set.spawn.mod.seed.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.world.gen.GeneratorOptions;
import net.set.spawn.mod.interfaces.SetSeedHolder;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;

import java.util.OptionalLong;

@Mixin(GeneratorOptions.class)
public abstract class GeneratorOptionsMixin implements SetSeedHolder {
    @Unique
    private boolean setSeed = false;

    @Override
    public boolean setspawnmod$isSetSeed() {
        return setSeed;
    }

    @Override
    public void setspawnmod$setSetSeed(boolean setSeed) {
        System.out.println("go set seed: " + setSeed);
        this.setSeed = setSeed;
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    @ModifyExpressionValue(
            method = "withSeed",
            at = @At(value = "NEW", target = "net/minecraft/world/gen/GeneratorOptions")
    )
    private GeneratorOptions setSetSeed(GeneratorOptions original, OptionalLong seed) {
        if (seed.isPresent()) {
            ((SetSeedHolder) original).setspawnmod$setSetSeed(true);
        }
        return original;
    }

    @ModifyExpressionValue(method = {"withBonusChest", "withStructures"}, at = @At(value = "NEW", target = "net/minecraft/world/gen/GeneratorOptions"))
    private GeneratorOptions curryGeneratorOptions(GeneratorOptions original) {
        System.out.println("currying go");
        ((SetSeedHolder) original).setspawnmod$setSetSeed(this.setspawnmod$isSetSeed());
        return original;
    }
}
