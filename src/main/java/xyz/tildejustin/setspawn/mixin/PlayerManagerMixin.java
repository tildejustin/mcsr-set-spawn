
package xyz.tildejustin.setspawn.mixin;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.ClientConnection;
import net.minecraft.server.PlayerManager;
import net.minecraft.text.LiteralText;
import xyz.tildejustin.setspawn.SetSpawn;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerManager.class)
public class PlayerManagerMixin {

    @Inject(method = "onPlayerConnect", at = @At("TAIL"))
    public void onPlayerConnect(ClientConnection connection, ServerPlayerEntity player, CallbackInfo ci) {
        if (SetSpawn.shouldSendErrorMessage) {
            LiteralText message = new LiteralText("§c" + SetSpawn.errorMessage + " This run is not verifiable.");
            player.sendMessage(message);
        }
        SetSpawn.shouldSendErrorMessage = false;
    }

}