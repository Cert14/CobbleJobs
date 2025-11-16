package com.cobblejobs.config;

import com.cobblejobs.CobbleJobsMod;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class JobsConfigLoader {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final String CONFIG_FOLDER = "cobblejobs";
    private static final String CONFIG_FILE = "jobs.json";

    public JobsConfig load() {
        Path configDir = FabricLoader.getInstance().getConfigDir().resolve(CONFIG_FOLDER);
        Path configPath = configDir.resolve(CONFIG_FILE);

        try {
            if (!Files.exists(configPath)) {
                Files.createDirectories(configDir);
                // Write default bundled config to disk
                try (InputStream in = getClass().getClassLoader()
                        .getResourceAsStream("cobblejobs/jobs.json")) {
                    if (in != null) {
                        Files.copy(in, configPath);
                    } else {
                        CobbleJobsMod.LOGGER.warn("Default jobs.json not found in resources");
                    }
                }
            }

            try (Reader reader = Files.newBufferedReader(configPath, StandardCharsets.UTF_8)) {
                JobsConfig config = GSON.fromJson(reader, JobsConfig.class);
                if (config == null) {
                    CobbleJobsMod.LOGGER.error("Failed to parse jobs.json, using empty config");
                    return new JobsConfig();
                }
                return config;
            }
        } catch (IOException e) {
            CobbleJobsMod.LOGGER.error("Failed to load CobbleJobs config", e);
            return new JobsConfig();
        }
    }
}

