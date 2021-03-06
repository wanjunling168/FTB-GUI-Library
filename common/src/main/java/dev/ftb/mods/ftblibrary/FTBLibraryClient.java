package dev.ftb.mods.ftblibrary;

import dev.ftb.mods.ftblibrary.icon.AtlasSpriteIcon;
import dev.ftb.mods.ftblibrary.icon.IconPresets;
import dev.ftb.mods.ftblibrary.icon.IconRenderer;
import dev.ftb.mods.ftblibrary.icon.Icons;
import dev.ftb.mods.ftblibrary.sidebar.SidebarButtonManager;
import dev.ftb.mods.ftblibrary.sidebar.SidebarGroupGuiButton;
import dev.ftb.mods.ftblibrary.util.ClientUtils;
import me.shedaniel.architectury.event.events.GuiEvent;
import me.shedaniel.architectury.event.events.TextureStitchEvent;
import me.shedaniel.architectury.event.events.client.ClientTickEvent;
import me.shedaniel.architectury.hooks.ScreenHooks;
import me.shedaniel.architectury.registry.ReloadListeners;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.world.InteractionResult;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * @author LatvianModder
 */
public class FTBLibraryClient extends FTBLibraryCommon {
	public static final List<IconRenderer<?>> ICON_RENDERERS = new ArrayList<>();
	public static int showButtons = 1;

	@Override
	public void init() {
		// Datagens hahayes
		if (Minecraft.getInstance() == null) {
			return;
		}

		TextureStitchEvent.PRE.register(this::textureStitch);
		GuiEvent.INIT_POST.register(this::guiInit);
		GuiEvent.RENDER_PRE.register((screen, matrices, mouseX, mouseY, delta) -> {
			renderTick();
			return InteractionResult.PASS;
		});
		ClientTickEvent.CLIENT_POST.register(this::clientTick);

		ReloadListeners.registerReloadListener(PackType.CLIENT_RESOURCES, SidebarButtonManager.INSTANCE);
	}

	private void textureStitch(TextureAtlas atlas, Consumer<ResourceLocation> addSprite) {
		if (!atlas.location().equals(TextureAtlas.LOCATION_BLOCKS)) {
			return;
		}

		try {
			for (Field field : Icons.class.getDeclaredFields()) {
				field.setAccessible(true);
				Object o = field.get(null);

				if (o instanceof AtlasSpriteIcon) {
					AtlasSpriteIcon a = (AtlasSpriteIcon) o;
					addSprite.accept(a.id);
					IconPresets.MAP.put(a.id.toString(), a);
				}
			}
		} catch (Exception ignored) {
		}
	}

	private void renderTick() {
		if (!ICON_RENDERERS.isEmpty()) {
			for (IconRenderer<?> iconRenderer : ICON_RENDERERS) {
				iconRenderer.render();
			}
		}
	}

	@SuppressWarnings("rawtypes")
	private void guiInit(Screen screen, List<AbstractWidget> abstractWidgets, List<GuiEventListener> guiEventListeners) {
		if (areButtonsVisible(screen)) {
			SidebarGroupGuiButton group = new SidebarGroupGuiButton((AbstractContainerScreen) screen);
			ScreenHooks.addButton(screen, group);
		}
	}

	private void clientTick(Minecraft client) {
		if (!ClientUtils.RUN_LATER.isEmpty()) {
			for (Runnable runnable : new ArrayList<>(ClientUtils.RUN_LATER)) {
				runnable.run();
			}

			ClientUtils.RUN_LATER.clear();
		}
	}

	public static boolean areButtonsVisible(@Nullable Screen gui) {
		if (showButtons == 0 || showButtons == 2 && !(gui instanceof EffectRenderingInventoryScreen)) {
			return false;
		}

		return gui instanceof AbstractContainerScreen && !SidebarButtonManager.INSTANCE.groups.isEmpty();
	}
}
