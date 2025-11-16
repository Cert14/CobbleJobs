package com.cobblejobs.command;

import com.cobblejobs.CobbleJobsMod;
import com.cobblejobs.config.JobDefinition;
import com.cobblejobs.player.PlayerJobData;
import com.cobblejobs.service.JobsService;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import java.util.Map;

public class JobsCommand {

    public static void register() {
        CommandRegistrationCallback.EVENT.register(JobsCommand::registerInternal);
    }

    private static void registerInternal(CommandDispatcher<CommandSourceStack> dispatcher,
                                         net.minecraft.core.HolderLookup.Provider registryAccess,
                                         net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback.CommandEnvironment env) {

        dispatcher.register(Commands.literal("jobs")
                .requires(source -> source.hasPermission(0))
                .executes(ctx -> listJobs(ctx.getSource()))
                .then(Commands.literal("list").executes(ctx -> listJobs(ctx.getSource())))
                .then(Commands.literal("browse").executes(ctx -> browseJobs(ctx.getSource())))
                .then(Commands.literal("join")
                        .then(Commands.argument("jobId", StringArgumentType.word())
                                .executes(ctx -> joinJob(ctx.getSource(), StringArgumentType.getString(ctx, "jobId")))))
                .then(Commands.literal("leave")
                        .then(Commands.argument("jobId", StringArgumentType.word())
                                .executes(ctx -> leaveJob(ctx.getSource(), StringArgumentType.getString(ctx, "jobId")))))
                .then(Commands.literal("info")
                        .then(Commands.argument("jobId", StringArgumentType.word())
                                .executes(ctx -> jobInfo(ctx.getSource(), StringArgumentType.getString(ctx, "jobId")))))
        );
    }

    private static int listJobs(CommandSourceStack source) {
        ServerPlayer player = source.getPlayer();
        if (player == null) {
            source.sendFailure(Component.literal("Only players can use this command."));
            return 0;
        }

        JobsService service = CobbleJobsMod.getJobsService();
        PlayerJobData data = service.getPlayerData(player.getUUID());

        if (data.getJobs().isEmpty()) {
            player.sendSystemMessage(Component.translatable("cobblejobs.command.jobs.none"));
            return 1;
        }

        player.sendSystemMessage(Component.translatable("cobblejobs.command.jobs.list"));
        for (Map.Entry<String, ?> entry : data.getJobs().entrySet()) {
            String jobId = entry.getKey();
            JobDefinition def = service.getJobsById().get(jobId);
            if (def != null) {
                player.sendSystemMessage(Component.literal("- " + def.getDisplayName() + " (" + def.getId() + ")"));
            }
        }

        return 1;
    }

    private static int browseJobs(CommandSourceStack source) {
        ServerPlayer player = source.getPlayer();
        if (player == null) {
            source.sendFailure(Component.literal("Only players can use this command."));
            return 0;
        }

        JobsService service = CobbleJobsMod.getJobsService();
        player.sendSystemMessage(Component.translatable("cobblejobs.command.jobs.available"));

        for (JobDefinition def : service.getJobsById().values()) {
            player.sendSystemMessage(Component.literal("- " + def.getDisplayName() + " (" + def.getId() + ")"));
        }

        return 1;
    }

    private static int joinJob(CommandSourceStack source, String jobId) {
        ServerPlayer player = source.getPlayer();
        if (player == null) {
            source.sendFailure(Component.literal("Only players can use this command."));
            return 0;
        }

        JobsService service = CobbleJobsMod.getJobsService();
        if (!service.getJobsById().containsKey(jobId)) {
            player.sendSystemMessage(Component.translatable("cobblejobs.command.jobs.not_found"));
            return 0;
        }

        PlayerJobData data = service.getPlayerData(player.getUUID());
        if (data.getJobs().containsKey(jobId)) {
            player.sendSystemMessage(Component.translatable("cobblejobs.command.jobs.already_joined"));
            return 0;
        }

        if (!service.canJoinMoreJobs(data)) {
            player.sendSystemMessage(Component.translatable("cobblejobs.command.jobs.limit_reached"));
            return 0;
        }

        boolean ok = service.joinJob(player, jobId);
        if (ok) {
            player.sendSystemMessage(Component.literal(
                    String.format("You joined the job %s.", jobId)
            ));
        } else {
            player.sendSystemMessage(Component.literal("Unable to join that job."));
        }

        return ok ? 1 : 0;
    }

    private static int leaveJob(CommandSourceStack source, String jobId) {
        ServerPlayer player = source.getPlayer();
        if (player == null) {
            source.sendFailure(Component.literal("Only players can use this command."));
            return 0;
        }

        JobsService service = CobbleJobsMod.getJobsService();
        boolean ok = service.leaveJob(player, jobId);
        if (ok) {
            player.sendSystemMessage(Component.literal(
                    String.format("You left the job %s.", jobId)
            ));
            return 1;
        } else {
            player.sendSystemMessage(Component.literal("You don't have that job."));
            return 0;
        }
    }

    private static int jobInfo(CommandSourceStack source, String jobId) {
        ServerPlayer player = source.getPlayer();
        if (player == null) {
            source.sendFailure(Component.literal("Only players can use this command."));
            return 0;
        }

        JobsService service = CobbleJobsMod.getJobsService();
        JobDefinition def = service.getJobsById().get(jobId);
        if (def == null) {
            player.sendSystemMessage(Component.translatable("cobblejobs.command.jobs.not_found"));
            return 0;
        }

        player.sendSystemMessage(Component.literal(
                String.format("Job: %s (%s)", def.getDisplayName(), def.getId())
        ));
        player.sendSystemMessage(Component.literal("Origin: " + def.getOrigin()));
        player.sendSystemMessage(Component.literal("Category: " + def.getCategory()));
        player.sendSystemMessage(Component.literal("Max Level: " + def.getMaxLevel()));

        return 1;
    }
}

