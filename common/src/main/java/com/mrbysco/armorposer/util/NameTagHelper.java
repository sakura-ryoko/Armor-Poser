package com.mrbysco.armorposer.util;

import com.mrbysco.armorposer.platform.Services;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.world.entity.decoration.ArmorStand;

public class NameTagHelper {
	public static boolean canRenderNameTag(EntityRenderDispatcher dispatcher, ArmorStand entity, double distanceToCameraSq) {
		if (Services.PLATFORM.directNametagOnly() && dispatcher.crosshairPickEntity != entity) {
			return false;
		}

		int nameRenderDistance = Services.PLATFORM.nametagRenderDistance();
		int maxSquaredDistance = nameRenderDistance * nameRenderDistance;
		if (maxSquaredDistance > 0) {
			return distanceToCameraSq < maxSquaredDistance;
		}
		return true;
	}
}
