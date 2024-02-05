package net.set.spawn.mod;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.Util;
import org.jetbrains.annotations.*;
import org.spongepowered.include.com.google.gson.*;

import java.io.*;
import java.nio.file.*;
import java.util.Arrays;

public class SetSpawn implements ClientModInitializer {
    private static final String MOD_ID = "setspawnmod";
    private static final String globalDir = SetSpawn.MOD_ID + "_global";
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    public static Path localConfigFile;
    public static Path globalConfigFile;
    public static Config config;

    static {
        globalConfigFile = getGlobalConfigFile();
        createIfNonExistent(globalConfigFile);
        localConfigFile = FabricLoader.getInstance().getConfigDir().resolve("setspawn.json");
        createIfNonExistent(globalConfigFile, true);
        createIfNonExistent(localConfigFile, true);
        try {
            loadProperties();
        } catch (IOException e) {
            // noinspection CallToPrintStackTrace
            e.printStackTrace();
        }
    }

    public static @Nullable Seed findSeedObjectFromLong(long seedLong) {
        String seed = String.valueOf(seedLong);
        Seed[] seedObjects = config.getSeeds();
        return Arrays.stream(seedObjects).filter(seedObject -> seedObject.getSeed().equals(seed)).findFirst().orElse(null);
    }

    private static void loadProperties() throws IOException, NumberFormatException, JsonSyntaxException {
        BufferedReader bufferedReader = new BufferedReader(new FileReader(localConfigFile.toFile()));
        config = gson.fromJson(bufferedReader, Config.class);
        if (config.isUseGlobalConfig()) {
            bufferedReader = new BufferedReader(new FileReader(globalConfigFile.toFile()));
            config = gson.fromJson(bufferedReader, Config.class);
        }
    }

    private static void writeDefaultProperties(Path file) throws IOException {
        Config config = getDefaultConfig();
        try (Writer writer = new FileWriter(file.toFile())) {
            gson.toJson(config, writer);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @NotNull
    private static Config getDefaultConfig() {
        Seed vine = new Seed("8398967436125155523", "vine", -201.5, 229.5);
        Seed taiga = new Seed("2483313382402348964", "taiga", -233.5, 246.5);
        Seed gravel = new Seed("-3294725893620991126", "gravel", 161.5, 194.5);
        Seed dolphin = new Seed("-4530634556500121041", "dolphin", 174.5, 200.5);
        Seed treasure = new Seed("7665560473511906728", "treasure", 90.5, 218.5);
        Seed rng = new Seed("-4810268054211229692", "rng", -153.5, 234.5);
        Seed arch = new Seed("2613428371297940758", "arch", -154.5, -217.5);
        Seed fletcher = new Seed("2478133068685386821", "fletcher", -248.5, 106.5);
        Seed boat = new Seed("-1771116315365891369", "boat", 218.5, 215.5);
        Seed[] seeds = new Seed[]{vine, taiga, gravel, dolphin, treasure, rng, arch, fletcher, boat};
        return new Config(true, false, seeds);
    }

    private static Path getGlobalConfigFile() {
        Path home = Paths.get(System.getProperty("user.home"));
        String filename = "setspawn.json";
        Util.OperatingSystem os = Util.getOperatingSystem();
        if (false /* os == OperatingSystem.LINUX */) {
            return home.resolve(".config").resolve(globalDir).resolve(filename);
        }
        return home.resolve(globalDir).resolve(filename);
    }

    private static void createIfNonExistent(Path file) {
        createIfNonExistent(file, false);
    }

    @SuppressWarnings("CallToPrintStackTrace")
    private static void createIfNonExistent(Path file, boolean writeConfig) {
        try {
            if (!Files.exists(file)) {
                Files.createDirectories(file.getParent());
                Files.createFile(file);
                if (writeConfig) {
                    writeDefaultProperties(file);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onInitializeClient() {
        // make files in static initializer if someone accesses this class before,
        // otherwise let it happen when fabric loads the entrypoint
    }
}
