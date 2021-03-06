package dev.ftb.mods.ftblibrary.ui.misc;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import dev.ftb.mods.ftblibrary.icon.Icon;
import dev.ftb.mods.ftblibrary.icon.Icons;
import dev.ftb.mods.ftblibrary.ui.GuiHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.gui.components.toasts.ToastComponent;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;

import java.util.List;

/**
 * @author LatvianModder
 */
public class SimpleToast implements Toast {
	private boolean hasPlayedSound = false;

	@Override
	public Visibility render(PoseStack matrixStack, ToastComponent gui, long delta) {
		GuiHelper.setupDrawing();
		Minecraft mc = gui.getMinecraft();
		mc.getTextureManager().bind(TEXTURE);
		RenderSystem.color4f(1F, 1F, 1F, 1F);
		gui.blit(matrixStack, 0, 0, 0, 0, 160, 32);

		List<FormattedCharSequence> list = mc.font.split(getSubtitle(), 125);
		int i = isImportant() ? 16746751 : 16776960;

		if (list.size() == 1) {
			mc.font.drawShadow(matrixStack, getTitle(), 30, 7, i | -16777216);
			mc.font.drawShadow(matrixStack, list.get(0), 30, 18, -1);
		} else {
			if (delta < 1500L) {
				int k = Mth.floor(Mth.clamp((float) (1500L - delta) / 300F, 0F, 1F) * 255F) << 24 | 67108864;
				mc.font.drawShadow(matrixStack, getTitle(), 30, 11, i | k);
			} else {
				int i1 = Mth.floor(Mth.clamp((float) (delta - 1500L) / 300F, 0F, 1F) * 252F) << 24 | 67108864;
				int l = 16 - list.size() * mc.font.lineHeight / 2;

				for (FormattedCharSequence s : list) {
					mc.font.drawShadow(matrixStack, s, 30, l, 16777215 | i1);
					l += mc.font.lineHeight;
				}
			}
		}

		if (!hasPlayedSound && delta > 0L) {
			hasPlayedSound = true;
			playSound(mc.getSoundManager());
		}

		GuiHelper.setupDrawing();
		Lighting.setupFor3DItems();
		getIcon().draw(matrixStack, 8, 8, 16, 16);
		return delta >= 5000L ? Toast.Visibility.HIDE : Toast.Visibility.SHOW;
	}

	public Component getTitle() {
		return new TextComponent("<error>");
	}

	public Component getSubtitle() {
		return TextComponent.EMPTY;
	}

	public boolean isImportant() {
		return false;
	}

	public Icon getIcon() {
		return Icons.INFO;
	}

	public void playSound(SoundManager handler) {
	}
}