package com.cobblejobs.economy;

import com.cobblejobs.player.PlayerJobData;
import com.cobblejobs.player.PlayerJobsRepository;
import net.minecraft.server.level.ServerPlayer;

import java.util.UUID;

public class InternalEconomyService implements EconomyService {

    private final PlayerJobsRepository repository;

    public InternalEconomyService(PlayerJobsRepository repository) {
        this.repository = repository;
    }

    @Override
    public boolean deposit(ServerPlayer player, double amount) {
        if (amount <= 0) {
            return false;
        }

        UUID uuid = player.getUUID();
        PlayerJobData data = repository.getOrCreate(uuid);
        data.setBalance(data.getBalance() + amount);
        repository.saveAsync(uuid, data);
        return true;
    }
}

