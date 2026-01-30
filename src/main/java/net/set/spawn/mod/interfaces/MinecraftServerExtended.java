package net.set.spawn.mod.interfaces;

import net.set.spawn.mod.SeedHolder;

public interface MinecraftServerExtended {
    SeedHolder setspawnmod$getSeedHolder();

    default void setspawnmod$setError(String error) {
        throw new UnsupportedOperationException();
    }

    default String setspawnmod$getError() {
        throw new UnsupportedOperationException();
    }
}
