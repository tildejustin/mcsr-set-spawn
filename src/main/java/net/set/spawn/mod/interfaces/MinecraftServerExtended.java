package net.set.spawn.mod.interfaces;

public interface MinecraftServerExtended {
    boolean setspawnmod$shouldModifySpawn();

    void setspawnmod$setShouldModifySpawn(boolean shouldModifySpawn);

    default void setspawnmod$setError(String error) {
    }

    default String setspawnmod$getError() {
        return "";
    }
}
