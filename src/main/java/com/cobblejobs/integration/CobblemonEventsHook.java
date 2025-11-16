package com.cobblejobs.integration;

import com.cobblejobs.CobbleJobsMod;
import com.cobblemon.mod.common.api.Priority;
import com.cobblemon.mod.common.api.events.CobblemonEvents;
import com.cobblemon.mod.common.pokemon.Pokemon;
import kotlin.Unit;
import net.minecraft.server.level.ServerPlayer;

public class CobblemonEventsHook {

    public static void register() {
        registerCaptureEvent();
        // TODO: Add battle win, breeding, and evolution events when available.
    }

    private static void registerCaptureEvent() {
        CobblemonEvents.POKEMON_CAPTURED.subscribe(Priority.NORMAL, event -> {
            ServerPlayer player = event.getPlayer();
            Pokemon pokemon = event.getPokemon();

            if (player != null && pokemon != null) {
                CobbleJobsMod.getJobsService().handlePokemonCaptured(player, pokemon);
            }

            return Unit.INSTANCE;
        });
    }
}

