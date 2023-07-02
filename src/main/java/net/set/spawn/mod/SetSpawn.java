package net.set.spawn.mod;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import org.spongepowered.include.com.google.gson.Gson;
import org.spongepowered.include.com.google.gson.GsonBuilder;
import org.spongepowered.include.com.google.gson.JsonSyntaxException;

import java.io.*;
import java.util.Objects;

public class SetSpawn implements ClientModInitializer {
    public static final String MOD_ID = "setspawnmod";
    public static final String subDir = SetSpawn.MOD_ID + "_global";
    public static boolean shouldModifySpawn;
    public static boolean shouldSendErrorMessage;
    public static String errorMessage;
    public static File localConfigFile;
    public static File globalConfigFile;
    public static Config config;

    public static void log(String message) {
        System.out.printf("[%s] %s", MOD_ID, message);
    }

    private static void createIfNonExistent(File file) {
        try {
            if (file.createNewFile()) {
                writeDefaultProperties(file);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private static void loadProperties() throws IOException, NumberFormatException, JsonSyntaxException {
        Gson gson = new Gson();
        BufferedReader bufferedReader = new BufferedReader(new FileReader(localConfigFile));
        config = gson.fromJson(bufferedReader, Config.class);
        if (config.isUseGlobalConfig()) {
            bufferedReader = new BufferedReader(new FileReader(globalConfigFile));
            config = gson.fromJson(bufferedReader, Config.class);
        }
    }

    private static void writeDefaultProperties(File file) throws IOException {
        Seed issp = new Seed("6995497596190477036", "ISSP", -748.5, 344.5);
        Seed[] seedsToWrite = new Seed[]{issp};
        Config config = new Config(true, false, seedsToWrite);

        try (Writer writer = new FileWriter(file)) {
            Gson gson = new GsonBuilder()
                    .setPrettyPrinting()
                    .create();
            gson.toJson(config, writer);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static Seed findSeedObjectFromLong(long seedLong) {
        String seed = String.valueOf(seedLong);
        Seed[] seedObjects = config.getSeeds();
        for (Seed seedObject : seedObjects) {
            if (Objects.equals(seedObject.getSeed(), seed)) {
                return seedObject;
            }
        }
        return null;
    }

    @Override
    public void onInitializeClient() {
        File globalDir = new File(System.getProperty("user.home").replace("\\", "/"), subDir);
        globalDir.mkdirs();
        globalConfigFile = new File(globalDir, "setspawn.json");
        localConfigFile = FabricLoader.getInstance().getConfigDir().resolve("setspawn.json").toFile();
        createIfNonExistent(globalConfigFile);
        createIfNonExistent(localConfigFile);
        try {
            loadProperties();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}