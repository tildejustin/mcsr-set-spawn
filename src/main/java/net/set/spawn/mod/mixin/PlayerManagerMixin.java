package net.set.spawn.mod.mixin;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.Connection;
import net.minecraft.server.PlayerManager;
import net.set.spawn.mod.SetSpawn;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.logging.Level;

@Mixin(PlayerManager.class)
public abstract class PlayerManagerMixin {
    /**
     * When the player connects, send an error message in the chat and log if there is one, and reset the error state.
     */
    @Inject(method = "onPlayerConnect", at = @At("TAIL"))
    public void onPlayerConnect(Connection connection, ServerPlayerEntity player, CallbackInfo ci) {
        if (SetSpawn.shouldSendErrorMessage) {
            player.method_3331(SetSpawn.errorMessage);
            SetSpawn.log(Level.WARNING, SetSpawn.errorMessage);
        }
        SetSpawn.shouldSendErrorMessage = false;
    }
}
