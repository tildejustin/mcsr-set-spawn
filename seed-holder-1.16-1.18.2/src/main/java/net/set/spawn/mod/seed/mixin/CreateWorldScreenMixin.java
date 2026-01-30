package net.set.spawn.mod.seed.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.gui.screen.world.CreateWorldScreen;
import net.minecraft.world.gen.GeneratorOptions;
import net.minecraft.world.level.LevelInfo;
import net.set.spawn.mod.interfaces.SetSeedHolder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;

@Mixin(CreateWorldScreen.class)
public abstract class CreateWorldScreenMixin {
    @ModifyVariable(
            method = "createLevel",
            at = @At(
                    value = "LOAD",
                    ordinal = 0
            )
    )
    private LevelInfo storeSetSeed(LevelInfo levelInfo, @Local GeneratorOptions options) {
        ((SetSeedHolder) (Object) levelInfo).setspawnmod$setSetSeed(((SetSeedHolder) options).setspawnmod$isSetSeed());
        return levelInfo;
    }
}
