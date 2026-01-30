package net.set.spawn.mod.mixin;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.set.spawn.mod.interfaces.MinecraftServerExtended;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin {
    @Shadow
    public abstract void sendMessage(Text message, boolean actionBar);

    @Shadow
    @Final
    private MinecraftServer server;

    @Inject(method = "onSpawn", at = @At("TAIL"))
    private void sendErrorMessage(CallbackInfo ci) {
        String setSpawnError = ((MinecraftServerExtended) this.server).setspawnmod$getError();
        if (setSpawnError != null) {
            this.sendMessage(Text.literal(setSpawnError + " This run is not verifiable.").formatted(Formatting.RED), false);
            ((MinecraftServerExtended) this.server).setspawnmod$setError(null);
        }
    }
}
