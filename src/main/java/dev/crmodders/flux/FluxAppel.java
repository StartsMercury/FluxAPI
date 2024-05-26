package dev.crmodders.flux;

import dev.crmodders.flux.events.OnRegisterLanguageEvent;
import dev.crmodders.flux.logging.LoggingAgent;
import dev.crmodders.flux.logging.api.MicroLogger;
import net.appel.mod.interfaces.ModInitializer;
import org.greenrobot.eventbus.Subscribe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FluxAppel implements ModInitializer {

    public static final MicroLogger LOGGER = LoggingAgent.getLogger("FluxAPI");

    @Override
    public void onInit() {
        LOGGER.info("Flux Initialized");
        FluxRegistries.EVENT_BUS.register(this);
    }

    @Subscribe
    public void onEvent(OnRegisterLanguageEvent event) {
        event.registerLanguage(FluxConstants.LanguageEnUs.load());
    }

    @Override
    public String getModId() {
        return "fluxapi";
    }
}
