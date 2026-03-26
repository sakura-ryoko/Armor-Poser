package com.mrbysco.armorposer.platform.services;

import com.mrbysco.armorposer.data.SwapData;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.decoration.ArmorStand;

import java.nio.file.Path;

public interface IPlatformHelper {
	/**
	 * Update Armor Stand Entity
	 */
	void updateEntity(ArmorStand armorStand, CompoundTag compound);

	/**
	 * Update Armor Stand Entity
	 */
	void swapSlots(ArmorStand armorStand, SwapData.Action action);

	/**
	 * Update Armor Stand Name
	 */
	void renameArmorStand(ArmorStand armorStand, String newName);

	/**
	 * Get the user preset folder
	 *
	 * @return The user preset folder
	 */
	Path getUserPresetFolder();

	/**
	 * Get the mod version
	 *
	 * @return The mod version
	 */
	String getModVersion();
}
