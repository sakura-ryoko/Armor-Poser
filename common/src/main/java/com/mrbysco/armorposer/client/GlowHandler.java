package com.mrbysco.armorposer.client;

import com.mrbysco.armorposer.client.debug.DebugHandler;
import net.minecraft.client.Minecraft;

import java.util.UUID;

public class GlowHandler {
	private static long glowStartTime = 0;
	private static UUID glowingStand = null;

	public static boolean shouldArmorStandGlow() {
		if (glowStartTime == -1) {
			return false;
		}
		boolean notEmpty = glowingStand != null;
		if (notEmpty && System.currentTimeMillis() - glowStartTime > 5000) {
			glowStartTime = -1;
			glowingStand = null;
		}
		return notEmpty;
	}

	public static boolean isGlowing(UUID uuid) {
		Minecraft minecraft = Minecraft.getInstance();
		if (minecraft.debugEntries.isCurrentlyEnabled(DebugHandler.SHOW_ARMOR_STANDS) && !minecraft.showOnlyReducedInfo()) {
			return true;
		}

		if (!shouldArmorStandGlow())
			return false;
		else
			return glowingStand != null && glowingStand.equals(uuid);
	}

	public static void startGlowing(UUID uuid) {
		glowStartTime = System.currentTimeMillis();
		glowingStand = uuid;
	}
}
