package xyz.tildejustin.setspawn.mixin;

import com.mojang.authlib.GameProfile;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import xyz.tildejustin.setspawn.Seed;
import xyz.tildejustin.setspawn.SetSpawn;

import static xyz.tildejustin.setspawn.SetSpawn.shouldModifySpawn;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin extends PlayerEntity {


    @Shadow @Final private static Logger LOGGER;

    public ServerPlayerEntityMixin(World world, GameProfile profile) {
        super(world, profile);
    }

    @ModifyVariable(method = "<init>", at = @At("STORE"), ordinal = 0)
    private BlockPos setspawn_setblockpos(BlockPos blockPos) {
        if (shouldModifySpawn) {
            SetSpawn.shouldModifySpawn = false;
            Seed seedObject = SetSpawn.findSeedObjectFromLong(this.world.getSeed());
            String response;
            if (seedObject != null) {
                int xFloor = MathHelper.floor(seedObject.getX());
                int zFloor = MathHelper.floor(seedObject.getZ());
                if ((Math.abs(xFloor - this.world.getSpawnPos().getX()) > MinecraftServer.getServer().getSpawnProtectionRadius() - 6) || (Math.abs(zFloor - this.world.getSpawnPos().getZ()) > MinecraftServer.getServer().getSpawnProtectionRadius() - 6)) {
                    SetSpawn.shouldSendErrorMessage = true;
                    response = "The X or Z coordinates given (" + seedObject.getX() + ", " + seedObject.getZ() + ") are more than 10 blocks away from the world spawn. Not overriding player spawnpoint.";
                    SetSpawn.errorMessage = response;
                    LOGGER.warn(response);
                } else {
                    // TODO: add fancy logging in modinit like wp
                    LOGGER.info("setting spawn");
                    return world.getTopPosition(new BlockPos(xFloor, 0, zFloor));
                }
            }
        }
        return blockPos;
    }
}