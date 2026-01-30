package net.set.spawn.mod.seed.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.gui.screen.world.*;
import net.minecraft.client.world.GeneratorOptionsHolder;
import net.minecraft.world.level.LevelInfo;
import net.set.spawn.mod.interfaces.SetSeedHolder;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.OptionalLong;

@Mixin(CreateWorldScreen.class)
public abstract class CreateWorldScreenMixin {
    @Shadow
    @Final
    WorldCreator worldCreator;

    @ModifyVariable(
            method = {"startServer", "createLevel"},
            at = @At(
                    value = "LOAD",
                    ordinal = 0
            ),
            require = 1
    )
    private LevelInfo storeSetSeed(LevelInfo levelInfo, @Local GeneratorOptionsHolder options) {
        ((SetSeedHolder) (Object) levelInfo).setspawnmod$setSetSeed(((SetSeedHolder) options.generatorOptions()).setspawnmod$isSetSeed());
        return levelInfo;
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    @Inject(method = "<init>*", at = @At("TAIL"))
    private void startingSeed(CallbackInfo ci, @Local(argsOnly = true) OptionalLong seed) {
        if (seed.isPresent()) {
            ((SetSeedHolder) this.worldCreator.getGeneratorOptionsHolder().generatorOptions()).setspawnmod$setSetSeed(true);
        }
    }
}
