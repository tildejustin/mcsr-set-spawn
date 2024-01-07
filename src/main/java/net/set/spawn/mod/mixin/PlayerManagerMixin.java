package net.set.spawn.mod.mixin;

import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.set.spawn.mod.SetSpawn;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(PlayerManager.class)
public class PlayerManagerMixin {
    @Redirect(
            method = {
                    // 1.19 - 23w18a
                    "Lnet/minecraft/server/PlayerManager;method_14570(Lnet/minecraft/network/ClientConnection;Lnet/minecraft/server/network/ServerPlayerEntity;)V",
                    // 23w31a - 23w35a
                    "Lnet/minecraft/server/PlayerManager;method_14570(Lnet/minecraft/network/ClientConnection;Lnet/minecraft/server/network/ServerPlayerEntity;I)V",
                    // 23w41a - present
                    "Lnet/minecraft/server/PlayerManager;method_14570(Lnet/minecraft/network/ClientConnection;Lnet/minecraft/server/network/ServerPlayerEntity;Lnet/minecraft/class_8792;)V"
            },
            at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayerEntity;onSpawn()V"),
            require = 0
    )
    private void onPlayerConnect(ServerPlayerEntity player) {
        player.onSpawn();
        if (SetSpawn.shouldSendErrorMessage) {
            Text message = Text.of("§c" + SetSpawn.errorMessage + " This run is not verifiable.");
            player.sendMessage(message, false);
        }
        SetSpawn.shouldSendErrorMessage = false;
    }
}