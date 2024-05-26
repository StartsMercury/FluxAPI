package dev.crmodders.flux;

import dev.crmodders.flux.entrypoints.FluxModInitializer;
import dev.crmodders.flux.logging.LoggingAgent;
import dev.crmodders.flux.logging.api.MicroLogger;

public class FluxAPI2 implements FluxModInitializer {

    public static final MicroLogger LOGGER = LoggingAgent.shallowCloneLoggerAs("FluxAPI", "FluxAPI2");

    @Override
    public void onInit() {
        LOGGER.info("Flux Initialized By Flux");
    }
}
