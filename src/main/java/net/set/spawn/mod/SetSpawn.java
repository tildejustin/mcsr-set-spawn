package net.set.spawn.mod;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.util.OperatingSystem;
import org.spongepowered.include.com.google.gson.*;

import java.io.*;
import java.nio.file.*;
import java.util.Arrays;
import java.util.logging.Level;

public class SetSpawn implements ClientModInitializer {
    private static final String MOD_ID = "setspawnmod";
    private static final String globalDir = SetSpawn.MOD_ID + "_global";
    public static boolean shouldModifySpawn;
    public static boolean shouldSendErrorMessage;
    public static String errorMessage;
    public static Path localConfigFile;
    public static Path globalConfigFile;
    public static Config config;
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

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

    @Override
    public void onInitializeClient() {
        // make files in static initializer if someone accesses this class before,
        // otherwise let it happen when fabric loads the entrypoint
    }

    public static Seed findSeedObjectFromLong(long seedLong) {
        String seed = String.valueOf(seedLong);
        Seed[] seedObjects = config.getSeeds();
        return Arrays.stream(seedObjects).filter(seedObject -> seedObject.getSeed().equals(seed)).findFirst().orElse(null);
    }

    public static void setError(boolean shouldSendErrorMessage, String errorMessage) {
        SetSpawn.shouldSendErrorMessage = shouldSendErrorMessage;
        SetSpawn.errorMessage = errorMessage + " §cThis run is not verifiable.";
    }

    public static void log(Level level, String message) {
        String hedgedMessage = String.format("[%s] %s", MOD_ID, message);
        if (level.equals(Level.WARNING) || level.equals(Level.SEVERE)) {
            System.err.println(hedgedMessage);
        } else {
            System.out.printf(hedgedMessage);
        }
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
        Seed issp = new Seed("6995497596190477036", "Internal Set Seed", -748.5, 344.5);
        Seed[] seedsToWrite = new Seed[]{issp};
        Config config = new Config(true, false, seedsToWrite);

        try (Writer writer = new FileWriter(file.toFile())) {
            gson.toJson(config, writer);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static Path getGlobalConfigFile() {
        Path home = Paths.get(System.getProperty("user.home"));
        String filename = "setspawn.json";
        OperatingSystem os = Minecraft.method_2940();
        if (os == OperatingSystem.LINUX) {
            return home.resolve(".config").resolve(globalDir).resolve(filename);
        }
        return home.resolve(filename);
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
}
