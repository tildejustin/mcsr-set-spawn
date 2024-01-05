package net.set.spawn.mod.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.world.*;
import net.minecraft.world.level.LevelInfo;
import net.set.spawn.mod.SetSpawn;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public abstract class MinecraftMixin {
    /**
     * Sets the spawn as modifiable only when in a new world. {@link SelectWorldScreen#joinWorld(int)} is where worlds are joined and calls {@link Minecraft#method_2935(String, String, LevelInfo)} with a null {@link LevelInfo}, unlike the call in {@link CreateWorldScreen}.
     */
    @Inject(method = "method_2935", at = @At("HEAD"))
    private void checkNewWorld(String savesDir, String levelName, LevelInfo levelInfo, CallbackInfo ci) {
        if (levelInfo != null) {
            SetSpawn.shouldModifySpawn = true;
        }
    }
}