package net.set.spawn.mod;

import org.spongepowered.include.com.google.gson.annotations.SerializedName;

public class Seed {
    private final String seed;
    @SerializedName("seed_name")
    private final String seedName;
    private final double x;
    private final double z;

    public Seed(String seed, String seedName, double x, double z) {
        this.seed = seed;
        this.seedName = seedName;
        this.x = x;
        this.z = z;
    }

    public Seed(String seed, double x, double z) {
        this(seed, "", x, z);
    }

    public String getSeed() {
        return seed;
    }

    public String getSeedName() {
        return seedName;
    }

    public double getX() {
        return x;
    }

    public double getZ() {
        return z;
    }
}
