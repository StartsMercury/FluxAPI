package dev.crmodders.flux.mixin.resource.loader;

import com.badlogic.gdx.files.FileHandle;
import com.llamalad7.mixinextras.sugar.Local;
import dev.crmodders.flux.impl.resource.loader.FluxAssetLoading;
import finalforeach.cosmicreach.GameAssetLoader;
import finalforeach.cosmicreach.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;
import java.util.HashSet;
import java.util.function.BiConsumer;

@Mixin(GameAssetLoader.class)
public class GameAssetLoaderMixin {
    @Final
    @Shadow
    private static HashMap<String, FileHandle> ALL_ASSETS;

    @Inject(
        method = "forEachAsset(Ljava/lang/String;Ljava/lang/String;Ljava/util/function/BiConsumer;Z)V",
        at = @At("RETURN")
    )
    private static void loadJarModAssets(
        final String prefixString,
        final String extension,
        final BiConsumer<String, FileHandle> assetConsumer,
        final boolean includeDirectories,
        final CallbackInfo callback,
        final @Local(ordinal = 0) HashSet<Identifier> allPaths
    ) {
        FluxAssetLoading.loadJarModAssets(
            prefixString,
            extension,
            assetConsumer,
            includeDirectories,
            ALL_ASSETS
        );
    }
}
