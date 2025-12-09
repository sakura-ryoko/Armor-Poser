package com.mrbysco.armorposer.mixin;

import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(AbstractSliderButton.class)
public interface AbstractSliderButtonAccessor {
	@Accessor("dragging")
	boolean armorposer$isDragging();

	@Accessor("dragging")
	void armorposer$setDragging(boolean dragging);

	@Invoker("getSprite")
	Identifier armorposer$getSprite();

	@Invoker("getHandleSprite")
	Identifier armorposer$getHandleSprite();
}
