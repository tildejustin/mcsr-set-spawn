package net.set.spawn.mod.mixin;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
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
    private int zFloor;
    private Seed seedObject;

    public ServerPlayerEntityMixin(World world) {
        super(world);
    }


    @ModifyVariable(method = "<init>", at = @At(value = "STORE", ordinal = 1), ordinal = 0)
    private int setspawn_setX(int x) {
        if (SetSpawn.shouldModifySpawn) {
            SetSpawn.shouldModifySpawn = false;
            this.seedObject = SetSpawn.findSeedObjectFromLong(this.world.getSeed());
            if (this.seedObject != null) {
                int xFloor = MathHelper.floor(this.seedObject.getX());
                this.zFloor = MathHelper.floor(this.seedObject.getZ());
                BlockPos spawnPos = this.world.getWorldSpawnPos();
                // lower bound = x + 0 - 10 + 0.5   or x - 9.5
                // higher bound = x + 19 - 10 + 0.5 or x + 9.5
                // floor(x) + 0.5 and floor(z) + 0.5 normalizes decimal that user puts in
                if (Math.abs(xFloor + 0.5 - spawnPos.x) > 10 || Math.abs(this.zFloor + 0.5 - spawnPos.z) > 10) {
                    SetSpawn.shouldSendErrorMessage = true;
                    SetSpawn.errorMessage = "The X or Z coordinates given (" + this.seedObject.getX() + ", " + this.seedObject.getZ() + ") are more than 10 blocks away from the world spawn. Not overriding player spawnpoint.";
                } else {
                    return xFloor;
                }
            }
        }
        return x;
    }

    @ModifyVariable(method = "<init>", at = @At(value = "STORE", ordinal = 1), ordinal = 1)
    private int setspawn_setZ(int z) {
        if (SetSpawn.shouldSendErrorMessage || this.seedObject == null) {
            return z;
        }
        return this.zFloor;
    }
}