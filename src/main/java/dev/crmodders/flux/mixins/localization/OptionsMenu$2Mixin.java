package dev.crmodders.flux.mixins.localization;

import dev.crmodders.flux.localization.TranslationKey;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import finalforeach.cosmicreach.gamestates.OptionsMenu;
import finalforeach.cosmicreach.ui.UISlider;

@Mixin(targets = "finalforeach.cosmicreach.gamestates.OptionsMenu$2", priority = 2000)
public abstract class OptionsMenu$2Mixin extends UISlider {

	public OptionsMenu$2Mixin(OptionsMenu this0, float min, float max, float defaultVal, float x, float y, float w, float h) {
		super(min, max, defaultVal, x, y, w, h);
	}

	@Unique
	private static final TranslationKey TEXT_RENDER_DISTANCE = new TranslationKey("options_menu.render_distance");

	@Override
	public void updateText() {
		setText(TEXT_RENDER_DISTANCE.getTranslated().format(String.valueOf((int)currentValue)));
	}

}
