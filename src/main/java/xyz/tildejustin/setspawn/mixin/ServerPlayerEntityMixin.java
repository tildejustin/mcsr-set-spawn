package xyz.tildejustin.setspawn.mixin;

import com.mojang.authlib.GameProfile;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.MinecraftServer;

import net.minecraft.server.network.ServerPlayerInteractionManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import xyz.tildejustin.setspawn.Seed;
import xyz.tildejustin.setspawn.SetSpawn;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin extends PlayerEntity {


    public ServerPlayerEntityMixin(World world, GameProfile profile) {
        super(world, profile);
    }

    @Inject(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/world/ServerWorld;getTopPosition(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/util/math/BlockPos;"), cancellable = true)
    public void setspawnmod_setSpawn(MinecraftServer server, ServerWorld world, GameProfile profile, ServerPlayerInteractionManager interactionManager, CallbackInfo ci) {
        if (SetSpawn.shouldModifySpawn) {
            SetSpawn.shouldModifySpawn = false;
            Seed seedObject = SetSpawn.findSeedObjectFromLong(world.getSeed());
            String response;
            if (seedObject != null) {
                int xFloor = MathHelper.floor(seedObject.getX());
                int zFloor = MathHelper.floor(seedObject.getZ());
                if ((Math.abs(xFloor - world.getSpawnPos().getX()) > server.getSpawnProtectionRadius() - 6) || (Math.abs(zFloor - world.getSpawnPos().getZ()) > server.getSpawnProtectionRadius() - 6)) {
                    SetSpawn.shouldSendErrorMessage = true;
                    response = "The X or Z coordinates given (" + seedObject.getX() + ", " + seedObject.getZ() + ") are more than 10 blocks away from the world spawn. Not overriding player spawnpoint.";
                    SetSpawn.errorMessage = response;
                    SetSpawn.LOGGER.warn(response);
                    ci.cancel();
                } else {
                    BlockPos this.blockPos = world.getTopPosition(new BlockPos(xFloor, 0, zFloor));
//                    if (spawnPos != null) {
//                        this.refreshPositionAndAngles(spawnPos, 0.0F, 0.0F);
//                        if (!world.doesBoxCollide(this, this.getBoundingBox()).isEmpty()) {
//                            SetSpawn.shouldSendErrorMessage = false;
//                            SetSpawn.LOGGER.info("Spawning player at: " + seedObject.getX() + " " + spawnPos.getY() + " " + seedObject.getZ());
//                            ci.cancel();
//                        } else {
//                            SetSpawn.shouldSendErrorMessage = true;
//                            response = "The coordinates given (" + seedObject.getX() + ", " + seedObject.getZ() + ") are obstructed by blocks. Not overriding player spawnpoint.";
//                            SetSpawn.errorMessage = response;
//                            SetSpawn.LOGGER.warn(response);
//                        }
//                    } else {
//                        SetSpawn.shouldSendErrorMessage = true;
//                        response = "There is no valid spawning location at the specified coordinates (" + seedObject.getX() + ", " + seedObject.getZ() + "). Not overriding player spawnpoint.";
//                        SetSpawn.errorMessage = response;
//                        SetSpawn.LOGGER.warn(response);
//                    }
                }
            }
        }
        return null;
    }

}