package dev.crmodders.flux;

import com.badlogic.gdx.files.FileHandle;
import dev.crmodders.flux.api.events.GameEvents;
import dev.crmodders.flux.localization.LanguageFile;
import dev.crmodders.flux.localization.TranslationApi;
import dev.crmodders.flux.logging.LogWrapper;
import finalforeach.cosmicreach.GameAssetLoader;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.entrypoint.PreLaunchEntrypoint;

public class FluxAPI implements ModInitializer, PreLaunchEntrypoint {

    @Override
    public void onPreLaunch() {
        GameEvents.ON_REGISTER_LANGUAGE.register(() -> {
            LanguageFile lang = LanguageFile.loadLanguageFile(FluxConstants.LanguageEnUs.load());
            TranslationApi.registerLanguage(lang);
        });
    }

    @Override
    public void onInitialize() {
        LogWrapper.init();
        LogWrapper.info("Flux Fabric Initialized");
    }
}
