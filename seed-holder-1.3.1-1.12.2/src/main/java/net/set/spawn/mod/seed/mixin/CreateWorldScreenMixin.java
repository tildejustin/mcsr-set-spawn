package net.set.spawn.mod.seed.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import net.minecraft.client.gui.screen.world.CreateWorldScreen;
import net.minecraft.world.level.LevelInfo;
import net.set.spawn.mod.interfaces.SetSeedHolder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;

@Mixin(CreateWorldScreen.class)
public abstract class CreateWorldScreenMixin {
    @ModifyVariable(
            method = "buttonClicked",
            at = @At(value = "STORE"),
            ordinal = 0,
            slice = @Slice(from = @At(value = "INVOKE", target = "Ljava/lang/Long;parseLong(Ljava/lang/String;)J"))
    )
    private long isSetSeed(long seed, @Share("setSeed") LocalBooleanRef setSeed) {
        setSeed.set(true);
        return seed;
    }

    @ModifyExpressionValue(
            method = "buttonClicked",
            at = @At(
                    value = "NEW",
                    target = "net/minecraft/world/level/LevelInfo" // signature changes
            )
    )
    private LevelInfo storeSetSeed(LevelInfo levelInfo, @Share("setSeed") LocalBooleanRef setSeed) {
        ((SetSeedHolder) (Object) levelInfo).setspawnmod$setSetSeed(setSeed.get());
        return levelInfo;
    }
}
