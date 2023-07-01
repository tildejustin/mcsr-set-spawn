package net.set.spawn.mod.mixin;

import com.mojang.authlib.GameProfile;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.MinecraftServer;
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
public abstract class ServerPlayerEntityMixin extends PlayerEntity {

    @Shadow @Final public MinecraftServer server;

    public ServerPlayerEntityMixin(World world, GameProfile profile) {
        super(world, profile);
    }

    @Inject(method = "method_21281", at = @At("HEAD"), cancellable = true)
    public void setSpawn(ServerWorld world, CallbackInfo ci) {
        if (SetSpawn.shouldModifySpawn) {
            SetSpawn.shouldModifySpawn = false;
            // method_3581 = getSeed
            Seed seedObject = SetSpawn.findSeedObjectFromLong(world.method_3581());
            String response;
            if (seedObject != null ) {
                int xFloor = MathHelper.floor(seedObject.getX());
                int zFloor = MathHelper.floor(seedObject.getZ());
                // method_3585 = getSpawnPos
                // method_12834 = getSpawnRadius
                if ((Math.abs(xFloor - world.method_3585().getX()) > this.server.method_12834(world))
                        || (Math.abs(zFloor - world.method_3585().getZ()) > this.server.method_12834(world))) {
                    SetSpawn.shouldSendErrorMessage = true;
                    response = "The X or Z coordinates given (" + seedObject.getX() + ", " + seedObject.getZ() + ") are more than 10 blocks away from the world spawn. Not overriding player spawnpoint.";
                    SetSpawn.errorMessage = response;
                    SetSpawn.LOGGER.warn(response);
                } else {
                    // method_16393 = getDimension
                    // method_17190 = getTopSpawningBlockPosition
                    BlockPos spawnPos = world.method_16393().method_17190(xFloor, zFloor, false);
                    if (spawnPos != null) {
                        this.refreshPositionAndAngles(spawnPos, 0.0F, 0.0F);
                        // method_16387 = doesNotCollide
                        if (world.method_16387(this, this.getBoundingBox())) {
                            SetSpawn.shouldSendErrorMessage = false;
                            SetSpawn.LOGGER.info("Spawning player at: " + seedObject.getX() + " " + spawnPos.getY() + " " + seedObject.getZ());
                            ci.cancel();
                        } else {
                            SetSpawn.shouldSendErrorMessage = true;
                            response = "The coordinates given (" + seedObject.getX() + ", " + seedObject.getZ() + ") are obstructed by blocks. Not overriding player spawnpoint.";
                            SetSpawn.errorMessage = response;
                            SetSpawn.LOGGER.warn(response);
                        }
                    } else {
                        SetSpawn.shouldSendErrorMessage = true;
                        response = "There is no valid spawning location at the specified coordinates (" + seedObject.getX() + ", " + seedObject.getZ() + "). Not overriding player spawnpoint.";
                        SetSpawn.errorMessage = response;
                        SetSpawn.LOGGER.warn(response);
                    }
                }
            }
        }
    }

}