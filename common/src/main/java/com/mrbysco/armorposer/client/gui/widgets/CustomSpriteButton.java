package com.mrbysco.armorposer.client.gui.widgets;

import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.SpriteIconButton;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.Nullable;

public class CustomSpriteButton extends SpriteIconButton.CenteredIcon {
	public CustomSpriteButton(int x, int y,
	                          Component message,
	                          int spriteWidth,
	                          int spriteHeight,
	                          WidgetSprites sprite,
	                          Button.OnPress onPress,
	                          @Nullable Component tooltip,
	                          Button.@Nullable CreateNarration createNarration) {
		super(20, 20, message, spriteWidth, spriteHeight, sprite, onPress, tooltip, createNarration);
		setPosition(x, y);
	}

	public CustomSpriteButton(int x, int y, int spriteWidth, int spriteHeight, WidgetSprites sprites, Button.OnPress onPress) {
		this(x, y, CommonComponents.EMPTY, spriteWidth, spriteHeight, sprites, onPress, null, null);
	}
}
