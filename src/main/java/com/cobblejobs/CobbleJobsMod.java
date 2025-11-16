package com.cobblejobs;

import com.cobblejobs.command.JobsCommand;
import com.cobblejobs.integration.CobblemonEventsHook;
import com.cobblejobs.service.JobsService;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CobbleJobsMod implements ModInitializer {

    public static final String MOD_ID = "cobblejobs";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    private static JobsService jobsService;

    @Override
    public void onInitialize() {
        LOGGER.info("Initializing CobbleJobs");

        jobsService = new JobsService();

        JobsCommand.register();

        CobblemonEventsHook.register();
    }

    public static JobsService getJobsService() {
        return jobsService;
    }
}
