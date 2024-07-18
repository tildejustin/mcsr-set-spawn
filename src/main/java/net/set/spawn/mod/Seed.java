package net.set.spawn.mod;

public class Seed {
    private final String seed;
    private final String seed_name;
    private final double x;
    private final double z;

    public Seed(String seed, String seed_name, double x, double z) {
        this.seed = seed;
        this.seed_name = seed_name;
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
        return seed_name;
    }

    public double getX() {
        return x;
    }

    public double getZ() {
        return z;
    }
}
