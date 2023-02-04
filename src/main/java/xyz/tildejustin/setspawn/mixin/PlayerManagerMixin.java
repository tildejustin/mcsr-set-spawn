package xyz.tildejustin.setspawn.mixin;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.ClientConnection;
import net.minecraft.server.PlayerManager;
import net.minecraft.text.LiteralText;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.tildejustin.setspawn.SetSpawn;

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

//    if you do At.shift.before it runs before first init
//    if you just do return, it runs after second init
//    I need it to run in between the two inits of ServerPlayerEntity (I'm not even sure where the second one is),
//    the only solution I could come up with was a counter, which is very inelegant

//    @Inject(method = "createPlayer", at = @At(value = "RETURN"))
//    public void setspawnmod_startedWorldGen(GameProfile par1, CallbackInfoReturnable<ServerPlayerEntity> cir){
//        SetSpawn.log(Level.INFO, "setting shouldmodify to true");
//        if (SetSpawn.config.isEnabled()) {
//            SetSpawn.shouldModifySpawn = true;
//        }
//    }

}