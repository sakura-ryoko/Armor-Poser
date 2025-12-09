package com.mrbysco.armorposer.client.debug;

import com.mrbysco.armorposer.Reference;
import com.mrbysco.armorposer.mixin.DebugScreenEntriesAccessor;
import net.minecraft.client.gui.components.debug.DebugEntryNoop;
import net.minecraft.resources.Identifier;

public class DebugHandler {
	public static final Identifier SHOW_ARMOR_STANDS = Reference.modLoc("show_armor_stands");

	public static void init() {
		registerDebugLine(SHOW_ARMOR_STANDS);
	}

	private static void registerDebugLine(Identifier resourceLocation) {
		DebugScreenEntriesAccessor.armorposer$register(resourceLocation, new DebugEntryNoop());
	}
}
