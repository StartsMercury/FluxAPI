package dev.crmodders.flux.mixin.resource.loader;

import com.badlogic.gdx.files.FileHandle;
import com.llamalad7.mixinextras.sugar.Local;
import dev.crmodders.flux.impl.resource.loader.AssetFinder;
import dev.crmodders.flux.impl.resource.loader.FluxFileHandle;
import finalforeach.cosmicreach.GameAssetLoader;
import finalforeach.cosmicreach.util.Identifier;
import org.jetbrains.annotations.Nullable;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.loader.api.QuiltLoader;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.IOException;
import java.nio.file.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.function.BiConsumer;

import static dev.crmodders.flux.impl.base.Logging.LOGGER;

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
        final var finder = flux_api$createAssetFinder(prefixString, extension, assetConsumer);
        if (finder == null) {
            return;
        }

        QuiltLoader.getAllMods()
            .stream()
            .filter(mod -> mod.getSourceType() != ModContainer.BasicSourceType.BUILTIN)
            .map(ModContainer::getSourcePaths)
            .flatMap(List::stream)
            .flatMap(List::stream)
            .map(Path::normalize)
            .forEach(root -> {
                if (Files.isDirectory(root)) {
                    finder.scan(root);
                    return;
                }
                try (final var zfs = FileSystems.newFileSystem(root)) {
                    finder.scan(zfs.getPath("/"));
                } catch (final ProviderNotFoundException cause) {
                    LOGGER.warn("No file system provider for {}", root, cause);
                } catch (final IOException cause) {
                    LOGGER.warn("Unable to access file system for {}", root, cause);
                }
            });
    }

    @Unique
    private static @Nullable AssetFinder flux_api$createAssetFinder(
        final String prefixNotation,
        final String extension,
        final BiConsumer<String, FileHandle> assetConsumer
    ) {
        final String namespace;
        final Path prefix;
        {
            final var separator = prefixNotation.indexOf(':');
            final String prefixString;

            if (separator >= 0) {
                namespace = prefixNotation.substring(0, separator);
                prefixString = prefixNotation.substring(separator + 1);
            } else {
                namespace = null;
                prefixString = prefixNotation;
            }

            try {
                prefix = Path.of(prefixString);
            } catch (final InvalidPathException cause) {
                LOGGER.warn("Invalid prefix path: {}", prefixNotation, cause);
                return null;
            }
        }

        return new AssetFinder(namespace, prefix, extension, (identifier, path) -> {
            final var handle = new FluxFileHandle(path);
            final var id = identifier.toString();
            ALL_ASSETS.put(id, handle);
            assetConsumer.accept(id, handle);
        });
    }
}
