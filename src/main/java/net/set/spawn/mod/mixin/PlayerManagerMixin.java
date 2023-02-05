package net.set.spawn.mod.mixin;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.Connection;
import net.minecraft.server.PlayerManager;
import net.minecraft.text.ChatMessage;
import net.set.spawn.mod.SetSpawn;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerManager.class)
public class PlayerManagerMixin {

    @Inject(method = "onPlayerConnect", at = @At("TAIL"))
    public void onPlayerConnect(Connection connection, ServerPlayerEntity player, CallbackInfo ci) {
        if (SetSpawn.shouldSendErrorMessage) {
            ChatMessage message = ChatMessage.createTextMessage("§c" + SetSpawn.errorMessage + " This run is not verifiable.");
            player.method_5505(message);
        }
        SetSpawn.shouldSendErrorMessage = false;
    }
}