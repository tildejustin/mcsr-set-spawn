package net.set.spawn.mod;

import org.spongepowered.include.com.google.gson.annotations.SerializedName;

public class Config {
    private final boolean enabled;
    @SerializedName("use_global_config")
    private final boolean useGlobalConfig;
    private final Seed[] seeds;

    public Config() {
        this(false, false, new Seed[0]);
    }

    public Config(boolean enabled, boolean useGlobalConfig, Seed[] seeds) {
        this.enabled = enabled;
        this.useGlobalConfig = useGlobalConfig;
        this.seeds = seeds;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public boolean isUseGlobalConfig() {
        return useGlobalConfig;
    }

    public Seed[] getSeeds() {
        return seeds;
    }
}
