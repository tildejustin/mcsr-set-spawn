package net.set.spawn.mod.mixin;

import net.minecraft.entity.player.*;
import net.minecraft.util.math.*;
import net.minecraft.world.World;
import net.set.spawn.mod.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin extends PlayerEntity {
    @Unique
    private int zFloor;

    @Unique
    private int xFloor;

    @Unique
    private boolean override;

    public ServerPlayerEntityMixin(World world) {
        super(world);
    }

    /**
     * Checks if the spawn can be overridden by checking if there is a config entry for the seed as well as checking the distance from origin.
     *
     * @return whether the spawn should be overridden or not
     */
    @Unique
    private boolean shouldOverride(long seed) {
        if (!SetSpawn.shouldModifySpawn) {
            return false;
        }
        SetSpawn.shouldModifySpawn = false;

        Seed seedObject = SetSpawn.findSeedObjectFromLong(seed);
        if (seedObject == null) {
            return false;
        }

        // normalize the decimal the user put in
        this.xFloor = MathHelper.floor(seedObject.getX());
        this.zFloor = MathHelper.floor(seedObject.getZ());
        BlockPos spawnPos = this.world.getWorldSpawnPos();

        // from Lnet/minecraft/entity/player/ServerPlayerEntity;<init>
        // BlockPos pos = world.getWorldSpawnPos();
        // int x = pos.x;
        // int z = pos.z;
        // x += this.random.nextInt(20) - 10;
        // z += this.random.nextInt(20) - 10;

        // therefore
        // x + 10 - pos.x is the value of the random call

        // check if the values are within random.nextInt(20)
        if (xFloor + 10 - spawnPos.x < 0 || xFloor + 10 - spawnPos.x > 19 || zFloor + 10 - spawnPos.z < 0 || zFloor + 10 - spawnPos.z > 19) {
            SetSpawn.setError(true, String.format("The X or Z coordinates given (%d, %d) are more than 10 blocks away from the world spawn (%d, %d). Not overriding player spawnpoint. ", xFloor, zFloor, spawnPos.x, spawnPos.z));
            return false;
        }
        return true;
    }

    @ModifyVariable(method = "<init>", at = @At(value = "STORE", ordinal = 1), ordinal = 0)
    private int setX(int original) {
        this.override = shouldOverride(this.world.getSeed());
        return this.override ? this.xFloor : original;
    }

    @ModifyVariable(method = "<init>", at = @At(value = "STORE", ordinal = 1), ordinal = 1)
    private int setZ(int original) {
        return this.override ? this.zFloor : original;
    }
}
