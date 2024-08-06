package net.set.spawn.mod.mixin;

import net.minecraft.network.ClientConnection;
import net.minecraft.server.*;
import net.minecraft.server.network.ServerPlayerEntity;
import net.set.spawn.mod.interfaces.MinecraftServerExtended;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerManager.class)
public abstract class PlayerManagerMixin {
    @Shadow
    @Final
    private MinecraftServer server;

    @Inject(method = "onPlayerConnect", at = @At("TAIL"))
    private void onPlayerConnect(ClientConnection connection, ServerPlayerEntity player, CallbackInfo ci) {
        ((MinecraftServerExtended) this.server).setspawnmod$setShouldModifySpawn(false);
    }
}
