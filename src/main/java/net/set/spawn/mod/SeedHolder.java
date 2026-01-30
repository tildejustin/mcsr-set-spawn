package net.set.spawn.mod;

public class SeedHolder {
    private boolean setSeed, newWorld, joined;

    public boolean shouldModifySpawn() {
        return setSeed && newWorld && !joined;
    }

    public void setNewWorld(boolean newWorld) {
        System.out.println("new world: " + newWorld);
        this.newWorld = newWorld;
    }

    public void setSetSeed(boolean setSeed) {
        System.out.println("set seed: " + setSeed);
        this.setSeed = setSeed;
    }

    public void setJoined(boolean joined) {
        System.out.println("joined: " + setSeed);
        this.joined = joined;
    }
}
