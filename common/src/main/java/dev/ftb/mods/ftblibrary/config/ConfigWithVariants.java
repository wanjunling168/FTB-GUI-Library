package dev.ftb.mods.ftblibrary.config;

import dev.ftb.mods.ftblibrary.ui.input.MouseButton;

/**
 * @author LatvianModder
 */
public abstract class ConfigWithVariants<T> extends ConfigValue<T> {
	public abstract T getIteration(T v, boolean next);

	@Override
	public void onClicked(MouseButton button, ConfigCallback callback) {
		if (value != null && getCanEdit()) {
			setCurrentValue(getIteration(value, button.isLeft()));
			callback.save(true);
		}
	}
}