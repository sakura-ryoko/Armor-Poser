package com.mrbysco.armorposer.client.gui.widgets;

import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.SpriteIconButton;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.Nullable;

public class CustomSpriteButton extends SpriteIconButton.CenteredIcon {
	public CustomSpriteButton(
			int x,
			int y,
			Component message,
			int spriteWidth,
			int spriteHeight,
			int spriteOffsetX,
			int spriteOffsetY,
			WidgetSprites sprite,
			Button.OnPress onPress,
			@Nullable Component tooltip,
			Button.@Nullable CreateNarration narration,
			boolean switchToLoadingAfterPress
	) {
		super(20, 20, message, spriteWidth, spriteHeight, spriteOffsetX, spriteOffsetY, sprite, onPress, tooltip, narration, switchToLoadingAfterPress);
		setPosition(x, y);
	}

	public CustomSpriteButton(int x, int y, int spriteWidth, int spriteHeight, WidgetSprites sprites, Button.OnPress onPress) {
		this(x, y, CommonComponents.EMPTY, spriteWidth, spriteHeight, 0, 0, sprites, onPress, null, null, false);
	}
}
