package net.set.spawn.mod.mixin;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.set.spawn.mod.Seed;
import net.set.spawn.mod.SetSpawn;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;


@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin extends PlayerEntity {
    private int xFloor;
    private int zFloor;
    private Seed seedObject;

    public ServerPlayerEntityMixin(World world, String string) {
        super(world, string);
    }

    @ModifyVariable(method = "<init>", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/world/World;method_3708(II)I"), ordinal = 0)
    private int setspawn_setX(int x) {
        if (SetSpawn.shouldModifySpawn) {
            SetSpawn.shouldModifySpawn = false;
            seedObject = SetSpawn.findSeedObjectFromLong(this.world.getSeed());
            String response;
            if (seedObject != null) {
                xFloor = MathHelper.floor(seedObject.getX());
                zFloor = MathHelper.floor(seedObject.getZ());
                BlockPos spawnPos = this.world.getWorldSpawnPos();
                int radius = MinecraftServer.getServer().getSpawnProtectionRadius() - 6;
                if (Math.abs(xFloor + 0.5 - spawnPos.x) > radius || Math.abs(zFloor + 0.5 - spawnPos.z) > radius) {
                    SetSpawn.shouldSendErrorMessage = true;
                    response = "The X or Z coordinates given (" + seedObject.getX() + ", " + seedObject.getZ() + ") are more than 10 blocks away from the world spawn. Not overriding player spawnpoint.";
                    SetSpawn.errorMessage = response;
                } else {
                    return xFloor;
                }
            }
        }
        return x;
    }

    @ModifyVariable(method = "<init>", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/world/World;method_3708(II)I"), ordinal = 1)
    private int setspawn_setZ(int z) {
        if (SetSpawn.shouldSendErrorMessage || seedObject == null) {
            return z;
        }
        return zFloor;
    }

    @ModifyVariable(method = "<init>", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/world/World;method_3708(II)I"), ordinal = 2)
    private int setspawn_setY(int y) {
        if (SetSpawn.shouldSendErrorMessage || seedObject == null) {
            return y;
        }
        return world.method_3708(xFloor, zFloor);
    }
}