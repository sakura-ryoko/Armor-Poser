package com.mrbysco.armorposer.util;

import com.mrbysco.armorposer.config.PoserConfig;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.world.entity.decoration.ArmorStand;

public class NameTagHelper {
	public static boolean canRenderNameTag(EntityRenderDispatcher dispatcher, ArmorStand entity, double distanceToCameraSq) {
		if (PoserConfig.CLIENT.directNametagOnly.get() && dispatcher.crosshairPickEntity != entity) {
			return false;
		}

		int nameRenderDistance = PoserConfig.CLIENT.nametagRenderDistance.getAsInt();
		int maxSquaredDistance = nameRenderDistance * nameRenderDistance;
		if (maxSquaredDistance > 0) {
			return distanceToCameraSq < maxSquaredDistance;
		}
		return true;
	}
}
