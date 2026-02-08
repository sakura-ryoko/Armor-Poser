package com.mrbysco.armorposer.platform.services;

import com.mrbysco.armorposer.data.SwapData;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.decoration.ArmorStand;

import java.nio.file.Path;
import java.util.List;

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
	 * Allow scrolling to increase/decrease the angle of text fields
	 */
	boolean allowScrolling();

	/**
	 * Get the user preset folder
	 *
	 * @return The user preset folder
	 */
	Path getUserPresetFolder();

	/**
	 * Check if a feature is restricted to ops only
	 *
	 * @param feature The feature to check
	 * @return If the feature is restricted to ops only
	 */
	boolean isRestrictedToOPS(String feature);

	/**
	 * Check if name based features are enabled
	 *
	 * @return If name based features are enabled
	 */
	boolean nameBasedFeatures();

	/**
	 * Gets a list of players that are allowed to resize the Armor Stand while restrictResizeToOP is enabled
	 *
	 * @return The resize whitelist
	 */
	@Deprecated
	List<? extends String> getResizeWhitelist();

	/**
	 * Gets a list of players that are allowed to use restricted features while they are restricted to ops only
	 *
	 * @return The restrict whitelist
	 */
	List<? extends String> getRestrictWhitelist();

	/**
	 * Get the mod version
	 *
	 * @return The mod version
	 */
	String getModVersion();

	/**
	 * Check if nametag rendering is set to direct only
	 *
	 * @return If nametag rendering is direct only
	 */
	boolean directNametagOnly();

	/**
	 * Get the nametag render distance
	 *
	 * @return The nametag render distance
	 */
	int nametagRenderDistance();
}
