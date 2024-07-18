package net.set.spawn.mod;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import org.spongepowered.include.com.google.gson.*;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.Objects;
import java.util.stream.Collectors;

public class SetSpawn implements ClientModInitializer {
    public static final String globalParentDir = "setspawnmod_global";
    public static Path globalConfigFile;
    public static Path localConfigFile;
    public static Config config;
    public static String remoteConfigContents;
    public static Gson gson = new GsonBuilder().setPrettyPrinting().create();

    private static void loadProperties() {
        try (BufferedReader reader = Files.newBufferedReader(localConfigFile, StandardCharsets.UTF_8)) {
            config = gson.fromJson(reader, Config.class);
            if (config.isUseGlobalConfig()) {
                try (BufferedReader globalReader = Files.newBufferedReader(globalConfigFile, StandardCharsets.UTF_8)) {
                    config = gson.fromJson(globalReader, Config.class);
                }
            }
        } catch (IOException e) {
            // bad config
            e.printStackTrace();
            config = new Config();
        }
    }

    private static void writeDefaultProperties(Path file) {
        if (remoteConfigContents == null) {
            remoteConfigContents = getRemoteConfigContents();
        }

        try (BufferedWriter writer = Files.newBufferedWriter(file, StandardCharsets.UTF_8)) {
            if (remoteConfigContents == null) {
                // no internet access
                gson.toJson(new Config(true, false, new Seed[0]), writer);
            } else {
                writer.write(remoteConfigContents);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getRemoteConfigContents() {
        URLConnection connection;
        try {
            connection = new URI("https://raw.githubusercontent.com/Minecraft-Java-Edition-Speedrunning/set-spawn-meta/main/setspawn.json").toURL().openConnection();
        } catch (IOException | URISyntaxException e) {
            throw new RuntimeException(e);
        }
        connection.setConnectTimeout(1000);
        connection.setReadTimeout(1000);
        String output = null;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
            output = reader.lines().collect(Collectors.joining("\n"));
        } catch (IOException e) {
            System.out.println("Set Spawn error: Connection took too long or could not be made!");
        }
        return output;
    }

    public static Seed findSeedObjectFromLong(long seedLong) {
        // reload config on every reset
        loadProperties();
        if (!config.isEnabled()) {
            return null;
        }
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
        globalConfigFile = Paths.get(System.getProperty("user.home"), globalParentDir, "setspawn.json");
        localConfigFile = FabricLoader.getInstance().getConfigDir().resolve("setspawn.json");
        try {
            Files.createDirectories(globalConfigFile.getParent());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if (Files.notExists(globalConfigFile)) {
            writeDefaultProperties(globalConfigFile);
        }
        if (Files.notExists(localConfigFile)) {
            writeDefaultProperties(localConfigFile);
        }
        loadProperties();
    }
}
