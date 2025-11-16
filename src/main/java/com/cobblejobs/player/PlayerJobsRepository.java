package com.cobblejobs.player;

import com.cobblejobs.CobbleJobsMod;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerJobsRepository {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final String DATA_FOLDER = "cobblejobs/playerdata";

    private final Map<UUID, PlayerJobData> cache = new ConcurrentHashMap<>();
    private final Path dataDir;

    public PlayerJobsRepository() {
        this.dataDir = FabricLoader.getInstance().getConfigDir().resolve(DATA_FOLDER);
        try {
            Files.createDirectories(dataDir);
        } catch (IOException e) {
            CobbleJobsMod.LOGGER.error("Failed to create CobbleJobs player data directory", e);
        }
    }

    public PlayerJobData getOrCreate(UUID uuid) {
        return cache.computeIfAbsent(uuid, this::load);
    }

    private PlayerJobData load(UUID uuid) {
        Path file = dataDir.resolve(uuid.toString() + ".json");
        if (!Files.exists(file)) {
            return new PlayerJobData();
        }

        try (Reader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
            PlayerJobData data = GSON.fromJson(reader, PlayerJobData.class);
            return data != null ? data : new PlayerJobData();
        } catch (IOException e) {
            CobbleJobsMod.LOGGER.error("Failed to read CobbleJobs data for " + uuid, e);
            return new PlayerJobData();
        }
    }

    public void saveAsync(UUID uuid, PlayerJobData data) {
        cache.put(uuid, data);
        CompletableFuture.runAsync(() -> save(uuid, data));
    }

    private void save(UUID uuid, PlayerJobData data) {
        Path file = dataDir.resolve(uuid.toString() + ".json");
        try {
            Files.createDirectories(dataDir);
            try (Writer writer = Files.newBufferedWriter(file, StandardCharsets.UTF_8)) {
                GSON.toJson(data, writer);
            }
        } catch (IOException e) {
            CobbleJobsMod.LOGGER.error("Failed to save CobbleJobs data for " + uuid, e);
        }
    }
}

