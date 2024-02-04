package net.set.spawn.mod.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.set.spawn.mod.Seed;
import net.set.spawn.mod.SetSpawn;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin {

    @Shadow
    @Final
    public MinecraftServer server;

    @Unique
    private String setSpawnError;

    @Shadow
    public abstract void sendMessage(Text message, boolean actionBar);

    @Inject(method = "moveToSpawn", at = @At("HEAD"))
    private void getSeedObject(ServerWorld world, CallbackInfo ci, @Share("seed") LocalRef<Seed> seed) {
        // checking playerdata being null ensures only setting the spawn on fresh worlds and only on the first evaluated playerspawn
        if (this.server.getSaveProperties().getPlayerData() == null) {
            seed.set(SetSpawn.findSeedObjectFromLong(world.getSeed()));
        }
    }

    @WrapOperation(method = "moveToSpawn", at = @At(value = "INVOKE", target = "Ljava/util/Random;nextInt(I)I"))
    private int setSpawn(Random random, int bounds, Operation<Integer> original, @Local(ordinal = 0) BlockPos worldSpawn, @Local(ordinal = 0) int spawnRadius, @Share("seed") LocalRef<Seed> seed, @Share("originalRandomResult") LocalRef<Integer> originalRandomResult) {
        int originalResult = original.call(random, bounds);

        Seed seedObject = seed.get();
        if (seedObject == null) {
            return originalResult;
        }

        // Transform x and z coordinates into correct Random#nextInt result.
        int result = ((MathHelper.floor(seedObject.getX()) - worldSpawn.getX()) + spawnRadius) + ((MathHelper.floor(seedObject.getZ()) - worldSpawn.getZ()) + spawnRadius) * (spawnRadius * 2 + 1);

        if (result >= 0 && result < bounds) {
            // we save the original result in case the set spawn is invalid, see fallbackOnInvalidSpawn
            originalRandomResult.set(originalResult);
            return result;
        } else {
            this.setSpawnError = "The X or Z coordinates given (" + seed.get().getX() + ", " + seed.get().getZ() + ") are more than the worlds spawn radius (" + spawnRadius + " blocks) away from the world spawn. Not overriding player spawnpoint.";
        }
        return originalResult;
    }

    @ModifyVariable(method = "moveToSpawn", at = @At(value = "LOAD", ordinal = 0), ordinal = 5)
    private int fallbackOnInvalidSpawn(int p, @Local(ordinal = 4) LocalIntRef o, @Share("seed") LocalRef<Seed> seed, @Share("originalRandomResult") LocalRef<Integer> originalRandomResult) {
        // checks if the for loop is on its second iteration (p == 1), meaning the setspawn given spawn was invalid
        // and restores the original result of Random#nextInt
        if (p == 1 && originalRandomResult.get() != null) {
            o.set(originalRandomResult.get());
            originalRandomResult.set(null);
            p = 0;

            this.setSpawnError = "There is no valid spawning location at the specified coordinates (" + seed.get().getX() + ", " + seed.get().getZ() + "). Not overriding player spawnpoint.";
        }
        return p;
    }

    @Inject(method = "moveToSpawn", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayerEntity;refreshPositionAndAngles(Lnet/minecraft/util/math/BlockPos;FF)V", ordinal = 1))
    private void failOnNonRandomSpawns(CallbackInfo ci, @Share("seed") LocalRef<Seed> seed) {
        if (seed.get() != null) {
            this.setSpawnError = "Failed to apply SetSpawn configuration because the spawn was not random. Not overriding player spawnpoint.";
        }
    }

    @Inject(method = "onSpawn", at = @At("TAIL"))
    private void sendErrorMessage(CallbackInfo ci) {
        if (this.setSpawnError != null) {
            this.sendMessage(new LiteralText("§c" + this.setSpawnError + " This run is not verifiable."), false);
            this.setSpawnError = null;
        }
    }
}