package com.cobblejobs.economy;

import net.minecraft.server.level.ServerPlayer;

public interface EconomyService {

    /**
     * Adds money to the specified player's balance.
     *
     * @param player the player
     * @param amount the amount to add (can be fractional)
     * @return true if the transaction was successful
     */
    boolean deposit(ServerPlayer player, double amount);
}

