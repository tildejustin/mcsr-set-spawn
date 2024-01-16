package net.set.spawn.mod.test;

import net.fabricmc.loader.api.entrypoint.PreLaunchEntrypoint;
import net.minecraft.util.math.*;

import java.util.Random;

public class SetSpawnTest implements PreLaunchEntrypoint {
    private final Random random = new Random();

    private void shouldOverride(double x, double z, FakeWorld world) {
        shouldOverride(x, z, world, false);
    }

    private void shouldOverride(double x, double z, FakeWorld world, boolean print) {
        // normalize the decimal the user put in
        int xFloor = MathHelper.floor(x);
        int zFloor = MathHelper.floor(z);
        BlockPos spawnPos = world.getWorldSpawnPos();

        // from Lnet/minecraft/entity/player/ServerPlayerEntity;<init>
        // BlockPos pos = world.getWorldSpawnPos();
        // int x = pos.x;
        // int z = pos.z;
        // x += this.random.nextInt(20) - 10;
        // z += this.random.nextInt(20) - 10;

        // therefore
        // x + 10 - pos.x == the value of the random call

        // check if the values are within random.nextInt(20)
        if (xFloor + 10 - spawnPos.x < 0 || xFloor + 10 - spawnPos.x > 19 || zFloor + 10 - spawnPos.z < 0 || zFloor + 10 - spawnPos.z > 19) {
            System.out.printf("The X or Z coordinates given (%d, %d) are more than 10 blocks away from the world spawn (%d, %d). Not overriding player spawnpoint.%n", xFloor, zFloor, spawnPos.x, spawnPos.z);
        } else if (print) {
            System.out.printf("valid, %d, %d\n", xFloor, zFloor);
        }
    }

    private void assertStatement(boolean b, String msg, Object... args) {
        if (!b) {
            System.err.printf(msg, args);
        }
    }

    @Override
    public void onPreLaunch() {
        FakeWorld world = new FakeWorld(-750, 354);
        BlockPos spawnPos = world.getWorldSpawnPos();

        // vanilla logic
        for (int i = 0; i < 10000; ++i) {
            int x = spawnPos.x;
            int y = spawnPos.z;
            x += this.random.nextInt(20) - 10;
            y += this.random.nextInt(20) - 10;
            shouldOverride(x, y, world);
        }

        System.out.println("test all legal values");
        for (int i = 0; i < 20; ++i) {
            for (int j = 0; j < 20; ++j) {
                shouldOverride(spawnPos.x - 10 + i, spawnPos.z - 10 + j, world);
            }
        }

        System.out.println("test all extremes");
        shouldOverride(world.x - 11, world.z, world);
        shouldOverride(world.x + 10, world.z, world);
        shouldOverride(world.x, world.z - 11, world);
        shouldOverride(world.x, world.z + 10, world);

        System.out.println("test decimal numbers");
        shouldOverride(world.x - 10.1, world.z, world);
        // should be true, floor to neg infinity makes -740.1 into -741
        shouldOverride(world.x + 9.1, world.z, world, true);
        shouldOverride(world.x, world.z - 9.9, world, true);
        shouldOverride(world.x, world.z + 8.9, world, true);
        System.exit(0);
    }

    static class FakeWorld {
        private final int x;
        private final int z;

        FakeWorld(int x, int z) {
            this.x = x;
            this.z = z;
        }

        private BlockPos getWorldSpawnPos() {
            return new BlockPos(this.x, 0, this.z);
        }
    }
}
