package xyz.tildejustin.setspawn;

public class Config {
    private boolean enabled;
    private Seed[] seeds;

    public Config() {
    }

    public Config(boolean enabled, boolean useGlobalConfig, Seed[] seeds) {
        this.enabled = enabled;
        this.seeds = seeds;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public Seed[] getSeeds() {
        return seeds;
    }
}