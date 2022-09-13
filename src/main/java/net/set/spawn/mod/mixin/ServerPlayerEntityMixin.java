package net.set.spawn.mod.mixin;

import com.mojang.authlib.GameProfile;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.ScreenHandlerListener;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.set.spawn.mod.Seed;
import net.set.spawn.mod.SetSpawn;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin extends PlayerEntity implements ScreenHandlerListener {

    @Shadow @Final public MinecraftServer server;

    public ServerPlayerEntityMixin(World world, BlockPos pos, float yaw, GameProfile profile) {
        super(world, pos, yaw, profile);
    }
    @Inject(method = "moveToSpawn", at = @At("HEAD"), cancellable = true)
    public void setspawnmod_setSpawn(ServerWorld world, CallbackInfo ci) {
        if (SetSpawn.shouldModifySpawn) {
            SetSpawn.shouldModifySpawn = false;
            Seed seedObject = SetSpawn.findSeedObjectFromLong(world.getSeed());
            if (seedObject != null ) {
                if ((Math.abs(seedObject.getX() - world.getSpawnPos().getX()) > this.server.getSpawnRadius(world))
                        || (Math.abs(seedObject.getZ() - world.getSpawnPos().getZ()) > this.server.getSpawnRadius(world))) {
                    SetSpawn.LOGGER.warn("Coordinates given were impossible. Make sure X and Z are not more than 10 blocks away from the world spawn. Not overriding player spawnpoint.");
                } else {
                    BlockPos spawnPos = SpawnLocatingAccessor.callFindOverworldSpawn(world, MathHelper.floor(seedObject.getX()), MathHelper.floor(seedObject.getZ()), false);
                    if (spawnPos != null) {
                        SetSpawn.LOGGER.info("Spawning player at: " + seedObject.getX() + " " + spawnPos.getY() + " " + seedObject.getZ());
                        this.refreshPositionAndAngles(spawnPos, 0.0F, 0.0F);
                        ci.cancel();
                    }
                }
            }
        }
    }

}
