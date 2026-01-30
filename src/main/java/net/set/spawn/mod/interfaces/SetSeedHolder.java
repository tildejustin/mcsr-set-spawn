package net.set.spawn.mod.interfaces;

/**
 * to get the knowledge that the seed is set, which is only known on the client-side when creating the world, we abuse the fact that minecraft sends a reference
 * of its LevelInfo (pre 1.16) or GeneratorOptions (1.16+) to the IntegratedServer by attaching data to that class and reading it out on the server
 */
public interface SetSeedHolder {
    boolean setspawnmod$isSetSeed();

    void setspawnmod$setSetSeed(boolean setSeed);
}
