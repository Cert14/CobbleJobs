package com.cobblejobs.service;

import com.cobblejobs.CobbleJobsMod;
import com.cobblejobs.config.JobDefinition;
import com.cobblejobs.config.JobEventType;
import com.cobblejobs.config.JobsConfig;
import com.cobblejobs.config.JobsConfigLoader;
import com.cobblejobs.economy.EconomyService;
import com.cobblejobs.economy.InternalEconomyService;
import com.cobblejobs.player.JobProgress;
import com.cobblejobs.player.PlayerJobData;
import com.cobblejobs.player.PlayerJobsRepository;
import com.cobblemon.mod.common.pokemon.Pokemon;
import net.minecraft.server.level.ServerPlayer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class JobsService {

    private final JobsConfig config;
    private final Map<String, JobDefinition> jobsById = new HashMap<>();
    private final PlayerJobsRepository repository;
    private final EconomyService economyService;

    public JobsService() {
        this.config = new JobsConfigLoader().load();
        if (config.getJobs() != null) {
            for (JobDefinition def : config.getJobs()) {
                jobsById.put(def.getId(), def);
            }
        }
        this.repository = new PlayerJobsRepository();
        this.economyService = new InternalEconomyService(repository);
        CobbleJobsMod.LOGGER.info("Loaded {} jobs from configuration", jobsById.size());
    }

    public JobsConfig getConfig() {
        return config;
    }

    public Map<String, JobDefinition> getJobsById() {
        return jobsById;
    }

    public PlayerJobData getPlayerData(UUID uuid) {
        return repository.getOrCreate(uuid);
    }

    public boolean canJoinMoreJobs(PlayerJobData data) {
        int max = config.getMaxJobsPerPlayer();
        if (max <= 0) {
            return true;
        }
        return data.getJobs().size() < max;
    }

    public boolean joinJob(ServerPlayer player, String jobId) {
        JobDefinition job = jobsById.get(jobId);
        if (job == null) {
            return false;
        }

        UUID uuid = player.getUUID();
        PlayerJobData data = repository.getOrCreate(uuid);

        if (data.getJobs().containsKey(jobId)) {
            return false;
        }

        if (!canJoinMoreJobs(data)) {
            return false;
        }

        data.getOrCreateProgress(jobId);
        repository.saveAsync(uuid, data);
        return true;
    }

    public boolean leaveJob(ServerPlayer player, String jobId) {
        UUID uuid = player.getUUID();
        PlayerJobData data = repository.getOrCreate(uuid);
        if (data.getJobs().remove(jobId) != null) {
            repository.saveAsync(uuid, data);
            return true;
        }
        return false;
    }

    public void handlePokemonCaptured(ServerPlayer player, Pokemon pokemon) {
        handleEvent(player, JobEventType.POKEMON_CAPTURED, pokemon, 0.0);
    }

    public void handleBattleWin(ServerPlayer player, double baseReward) {
        handleEvent(player, JobEventType.BATTLE_WIN, null, baseReward);
    }

    private void handleEvent(ServerPlayer player, JobEventType type, Pokemon pokemon, double baseReward) {
        UUID uuid = player.getUUID();
        PlayerJobData data = repository.getOrCreate(uuid);

        if (data.getJobs().isEmpty()) {
            return;
        }

        List<JobDefinition> jobDefinitions = config.getJobs();
        if (jobDefinitions == null || jobDefinitions.isEmpty()) {
            return;
        }

        for (Map.Entry<String, JobProgress> entry : data.getJobs().entrySet()) {
            String jobId = entry.getKey();
            JobProgress progress = entry.getValue();
            JobDefinition def = jobsById.get(jobId);
            if (def == null || def.getActions() == null) {
                continue;
            }

            def.getActions().stream()
                    .filter(a -> a.getEvent() == type)
                    .forEach(action -> applyReward(player, data, progress, def, action, pokemon, baseReward));
        }

        repository.saveAsync(uuid, data);
    }

    private void applyReward(ServerPlayer player,
                             PlayerJobData data,
                             JobProgress progress,
                             JobDefinition def,
                             com.cobblejobs.config.JobActionDefinition action,
                             Pokemon pokemon,
                             double baseReward) {

        double money = action.getBaseMoney();
        double xp = action.getBaseXp();

        if (baseReward > 0 && money == 0) {
            money = baseReward;
        }

        if (money > 0) {
            boolean ok = economyService.deposit(player, money);
            if (!ok) {
                CobbleJobsMod.LOGGER.warn("Failed to pay {} {} for job {}", player.getName().getString(), money, def.getId());
            }
        }

        if (xp > 0) {
            progress.addXp(xp, def.getMaxLevel());
        }
    }
}

