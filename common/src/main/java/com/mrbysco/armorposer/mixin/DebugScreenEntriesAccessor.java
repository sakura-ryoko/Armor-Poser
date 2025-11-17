package com.mrbysco.armorposer.mixin;

import net.minecraft.client.gui.components.debug.DebugScreenEntries;
import net.minecraft.client.gui.components.debug.DebugScreenEntry;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(DebugScreenEntries.class)
public interface DebugScreenEntriesAccessor {

	@Invoker("register")
	static ResourceLocation armorposer$register(ResourceLocation name, DebugScreenEntry entry) {
		throw new AssertionError();
	}
}
