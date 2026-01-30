package net.set.spawn.mod.mixin;

import net.minecraft.server.MinecraftServer;
import net.set.spawn.mod.SeedHolder;
import net.set.spawn.mod.interfaces.MinecraftServerExtended;
import org.spongepowered.asm.mixin.*;

@Mixin(MinecraftServer.class)
public abstract class MinecraftServerMixin implements MinecraftServerExtended {
    @Unique
    private final SeedHolder seedHolder = new SeedHolder();

    @Override
    public SeedHolder setspawnmod$getSeedHolder() {
        return seedHolder;
    }
}
