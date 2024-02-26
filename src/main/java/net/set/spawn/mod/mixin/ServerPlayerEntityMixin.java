package net.set.spawn.mod.mixin;

import com.mojang.authlib.GameProfile;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.*;
import net.minecraft.world.World;
import net.set.spawn.mod.*;
import org.apache.logging.log4j.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin extends PlayerEntity {
    public ServerPlayerEntityMixin(World world, GameProfile gameProfile) {
        super(world, gameProfile);
    }

    @ModifyVariable(method = "<init>", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/server/world/ServerWorld;method_0_429(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/util/math/BlockPos;"))
    private BlockPos setspawn_setblockpos(BlockPos blockPos) {
        if (!SetSpawn.shouldModifySpawn) {
            return blockPos;
        }
        SetSpawn.shouldModifySpawn = false;


        Seed seedObject = SetSpawn.findSeedObjectFromLong(this.world.method_8412());
        if (seedObject == null) {
            return blockPos;
        }

        int xFloor = MathHelper.floor(seedObject.getX());
        int zFloor = MathHelper.floor(seedObject.getZ());
        BlockPos spawnPos = world.method_8395();
        @SuppressWarnings("DataFlowIssue")
        int spawnRadius = Math.max(0, world.method_8503().getSpawnRadius((ServerWorld) world));
        int worldBorderRadius = MathHelper.floor(world.method_8621().getDistanceInsideBorder(blockPos.getX(), blockPos.getZ()));

        if (worldBorderRadius < spawnRadius) {
            spawnRadius = worldBorderRadius;
        }
        if (worldBorderRadius <= 1) {
            spawnRadius = 1;
        }

        // serverWorld.method_0_429(blockPos.method_10069(this.random.nextInt(i * 2 + 1) - i, 0, this.random.nextInt(i * 2 + 1) - i));

        int bound = spawnRadius * 2 + 1;
        // spawnPos + rand(bound) - i
        int xRand = xFloor - spawnPos.getX() + spawnRadius;
        int zRand = zFloor - spawnPos.getZ() + spawnRadius;
        if ((xRand >= 0 && xRand < bound) && (zRand >= 0 && zRand < bound)) {
            SetSpawn.log(Level.INFO, "Setting spawn");
            return new BlockPos(xFloor, 0, zFloor);
        } else {
            SetSpawn.shouldSendErrorMessage = true;
            SetSpawn.errorMessage = "The X or Z coordinates given (" + seedObject.getX() + ", " + seedObject.getZ() + ") are more than 10 blocks away from the world spawn. Not overriding player spawnpoint.";
            return blockPos;
        }
    }
}
