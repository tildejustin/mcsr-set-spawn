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

        this.xFloor = MathHelper.floor(seedObject.getX());
        this.zFloor = MathHelper.floor(seedObject.getZ());
        BlockPos spawnPos = this.world.getWorldSpawnPos();

        // lower bound = x + 0 - 10 + 0.5   or x - 9.5
        // higher bound = x + 19 - 10 + 0.5 or x + 9.5
        // floor(x) + 0.5 and floor(z) + 0.5 normalizes decimal that user puts in
        if (Math.abs(xFloor + 0.5 - spawnPos.x) > 10 || Math.abs(this.zFloor + 0.5 - spawnPos.z) > 10) {
            SetSpawn.setError(true, "The X or Z coordinates given (" + seedObject.getX() + ", " + seedObject.getZ() + ") are more than 10 blocks away from the world spawn. Not overriding player spawnpoint.");
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
