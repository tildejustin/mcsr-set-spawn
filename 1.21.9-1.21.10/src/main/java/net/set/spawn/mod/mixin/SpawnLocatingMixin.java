package net.set.spawn.mod.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.*;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.SpawnLocating;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.*;
import net.minecraft.util.math.random.Random;
import net.set.spawn.mod.*;
import net.set.spawn.mod.interfaces.MinecraftServerExtended;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;

@Mixin(SpawnLocating.class)
public abstract class SpawnLocatingMixin {
    @Shadow
    @Final
    private ServerWorld world;

    @Shadow
    @Final
    private int spawnRadius;

    @Shadow
    @Final
    private BlockPos spawnPos;

    @Shadow
    private int attempt;

    @Mutable
    @Shadow
    @Final
    private int offset;

    @Unique
    private MinecraftServer server;

    @Unique
    private final ThreadLocal<Seed> seed = new ThreadLocal<>();

    @Unique
    private final ThreadLocal<Integer> originalRandomResult = new ThreadLocal<>();

    @Dynamic
    @WrapOperation(
            method = "<init>",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/util/math/random/Random;nextInt(I)I"
            )
    )
    private int setSpawn(Random random, int bounds, Operation<Integer> original) {
        server = this.world.getServer();

        int originalResult = original.call(random, bounds);

        SeedHolder seedHolder = ((MinecraftServerExtended) this.server).setspawnmod$getSeedHolder();
        if (seedHolder.shouldModifySpawn()) {
            seedHolder.setJoined(true); // TODO: change for WP
            seed.set(SetSpawn.findSeedObjectFromLong(this.world.getSeed()));
        }
        Seed seedObject = seed.get();
        if (seedObject == null) {
            return originalResult;
        }

        // Transform x and z coordinates into corresponding Random#nextInt result.
        int spawnDiameter = this.spawnRadius * 2 + 1;
        int x = MathHelper.floor(seedObject.getX());
        int z = MathHelper.floor(seedObject.getZ());
        int xLocal = x - this.spawnPos.getX() + spawnRadius;
        int result = xLocal + (z - this.spawnPos.getZ() + spawnRadius) * spawnDiameter;

        if (xLocal >= 0 && xLocal < spawnDiameter && result >= 0 && result < bounds) {
            // we save the original result in case the set spawn is invalid, see fallbackOnInvalidSpawn
            originalRandomResult.set(originalResult);
            System.out.println("Setting spawn");
            return result;
        } else {
            ((MinecraftServerExtended) server).setspawnmod$setError("The X or Z coordinates given (" + seed.get().getX() + ", " + seed.get().getZ() + ") are more than the worlds spawn radius (" + spawnRadius + " blocks) away from the world spawn. Not overriding player spawnpoint.");
        }
        return originalResult;
    }

    @ModifyVariable(
            method = "scheduleNextSearch",
            at = @At(
                    value = "LOAD",
                    ordinal = 0
            ),
            ordinal = 0
    )
    private int fallbackOnInvalidSpawn(int original) {
        // checks if the for loop is on its second iteration (p == 1), meaning the setspawn given spawn was invalid
        // and restores the original result of Random#nextInt
        if (original == 1 && originalRandomResult.get() != null) {
            this.offset = originalRandomResult.get();
            originalRandomResult.remove();
            // one effective post increment
            original = 0;
            this.attempt = 1;

            ((MinecraftServerExtended) server).setspawnmod$setError("There is no valid spawning location at the specified coordinates (" + seed.get().getX() + ", " + seed.get().getZ() + "). Not overriding player spawnpoint.");
        }
        return original;
    }
}
