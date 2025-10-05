package com.mrbysco.armorposer.util;

import com.mrbysco.armorposer.mixin.ArmorStandAccessor;
import com.mrbysco.armorposer.platform.Services;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.item.ItemStack;

import java.util.Locale;

/**
 * Utility class for handling default settings for armor stands.
 */
public class PoseDefaults {
	/**
	 * Adjust the armor stand based on the item stack's NBT data.
	 *
	 * @param stack      the item stack containing the pose data
	 * @param armorStand the armor stand to adjust
	 */
	public static void adjustArmorStand(ItemStack stack, ArmorStand armorStand) {
		if (Services.PLATFORM.nameBasedFeatures() && stack.getCustomName() != null) {
			final String name = stack.getCustomName().getString().toLowerCase(Locale.ROOT);
			switch (name) {
				case "armstrong", "arms":
					armorStand.setShowArms(true);
					break;
				case "based", "baseless":
					armorStand.setNoBasePlate(true);
					break;
				case "bdoubleo100", "bdubz", "small":
					((ArmorStandAccessor) armorStand).armorposer$setSmall(true);
					break;
				case "levitation":
					armorStand.setNoGravity(true);
					break;
				default:
			}
		}
	}
}
