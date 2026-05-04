package com.mrbysco.armorposer.client;

import com.mrbysco.armorposer.Reference;
import com.mrbysco.armorposer.platform.Services;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.UUID;

/**
 * Manages groups of armor stands for the Armor Poser mod.
 * Each group is identified by a unique name and contains a list of armor stand UUIDs.
 */
public class GroupHelper {

	// A map of default group names to their corresponding chat formatting colors.
	public static final Map<String, ChatFormatting> DEFAULT_GROUP_MAP = getDefaultMap();

	/**
	 * Initializes the default group map with predefined group names and their associated chat formatting colors.
	 *
	 * @return a map of default group names to their corresponding chat formatting colors
	 */
	private static Map<String, ChatFormatting> getDefaultMap() {
		Map<String, ChatFormatting> defaultMap = new TreeMap<>();
		defaultMap.put("group 1", ChatFormatting.RED);
		defaultMap.put("group 2", ChatFormatting.GREEN);
		defaultMap.put("group 3", ChatFormatting.BLUE);
		defaultMap.put("group 4", ChatFormatting.YELLOW);
		defaultMap.put("group 5", ChatFormatting.AQUA);
		defaultMap.put("group 6", ChatFormatting.LIGHT_PURPLE);
		defaultMap.put("group 7", ChatFormatting.GOLD);
		defaultMap.put("group 8", ChatFormatting.DARK_PURPLE);
		return defaultMap;
	}

	// A list of default group names that can be used for grouping armor stands.
	public static final List<String> DEFAULT_GROUP_NAMES = List.of("1", "2", "3", "4", "5", "6", "7", "8");

	// A map that associates group names with lists of armor stand UUIDs. This is the main data structure for managing groups of armor stands.
	private static final Map<String, List<UUID>> groupMap = new HashMap<>();

	/**
	 * Adds an armor stand to a specified group. If the group does not exist, it will be created.
	 *
	 * @param groupName  the name of the group to add the armor stand to
	 * @param armorStand the armor stand to add to the group
	 */
	public static void addToGroup(String groupName, ArmorStand armorStand) {
		UUID uuid = armorStand.getUUID();
		addToGroupLocal(groupName, uuid);
		Services.PLATFORM.updateEntityGroups(armorStand, getGroupsForArmorStand(uuid));
	}

	/**
	 * Adds an armor stand to a specified group without sending an update packet. This is used for local updates when loading group information from an armor stand's custom data.
	 *
	 * @param groupName    the name of the group to add the armor stand to
	 * @param armorStandId the UUID of the armor stand to add to the group
	 */
	public static void addToGroupLocal(String groupName, UUID armorStandId) {
		groupMap.computeIfAbsent(groupName, _ -> new ArrayList<>()).add(armorStandId);
	}

	/**
	 * Removes an armor stand from a specified group. If the group becomes empty after removal, it will be deleted.
	 *
	 * @param groupName  the name of the group to remove the armor stand from
	 * @param armorStand the armor stand to remove from the group
	 */
	public static void removeFromGroup(String groupName, ArmorStand armorStand) {
		UUID uuid = armorStand.getUUID();
		List<UUID> group = groupMap.get(groupName);
		if (group != null) {
			group.remove(uuid);
			if (group.isEmpty()) {
				groupMap.remove(groupName);
			}
		}
		Services.PLATFORM.updateEntityGroups(armorStand, getGroupsForArmorStand(uuid));
	}

	/**
	 * Retrieves a list of group names that a specified armor stand belongs to.
	 *
	 * @param armorStandId the UUID of the armor stand to check for group membership
	 * @return a list of group names that the armor stand belongs to, or an empty list if the armor stand does not belong to any groups
	 */
	public static List<String> getGroupsForArmorStand(UUID armorStandId) {
		List<String> groups = new ArrayList<>();
		for (Map.Entry<String, List<UUID>> entry : groupMap.entrySet()) {
			if (entry.getValue().contains(armorStandId)) {
				groups.add(entry.getKey());
			}
		}
		groups.sort(String::compareTo); // Just sorting the group so they display nicely
		return groups;
	}

	/**
	 * Retrieves the chat formatting color associated with a specified group name.
	 *
	 * @param groupName the name of the group to retrieve the chat formatting color for
	 * @return the chat formatting color associated with the group name, or WHITE if the group name is not found in the default group map
	 */
	public static ChatFormatting getFormatForGroup(String groupName) {
		return DEFAULT_GROUP_MAP.getOrDefault(groupName, ChatFormatting.WHITE);
	}

	/**
	 * Retrieves the list of armor stand UUIDs associated with a specified group.
	 *
	 * @param groupName the name of the group to retrieve
	 * @return a list of UUIDs for the armor stands in the group, or an empty list if the group does not exist
	 */
	public static List<UUID> getGroup(String groupName) {
		return groupMap.getOrDefault(groupName, List.of());
	}

	/**
	 * Clears all armor stands from a specified group and updates the entities in the provided level accordingly.
	 *
	 * @param groupName the name of the group to clear
	 * @param level     the level containing the entities to update after clearing the group
	 */
	public static void clearGroup(String groupName, Level level) {
		List<UUID> members = groupMap.getOrDefault(groupName, List.of());
		groupMap.remove(groupName);
		for (UUID uuid : new ArrayList<>(members)) {
			Entity entity = level.getEntity(uuid);
			if (entity instanceof ArmorStand armorStand) {
				Services.PLATFORM.updateEntityGroups(armorStand, getGroupsForArmorStand(uuid));
			}
		}
	}

	/**
	 * Checks if a specified armor stand is part of a specified group.
	 *
	 * @param group the name of the group to check
	 * @param uuid  the UUID of the armor stand to check for membership in the group
	 * @return true if the armor stand is part of the group, false otherwise
	 */
	public static boolean isInGroup(String group, UUID uuid) {
		return groupMap.getOrDefault(group, List.of()).contains(uuid);
	}

	/**
	 * Removes any armor stands from all groups that are not present in the provided set of valid UUIDs.
	 *
	 * @param valid a set of valid armor stand UUIDs to retain in the groups
	 */
	public static void pruneInvalid(Set<UUID> valid) {
		for (Map.Entry<String, List<UUID>> entry : groupMap.entrySet()) {
			entry.getValue().removeIf(uuid -> !valid.contains(uuid));
		}
	}

	/**
	 * Checks if a specified group is empty, meaning it has no armor stands associated with it.
	 *
	 * @param group the name of the group to check for emptiness
	 * @return true if the group is empty or does not exist, false if the group has one or more armor stands associated with it
	 */
	public static boolean isEmpty(String group) {
		return groupMap.getOrDefault(group, List.of()).isEmpty();
	}

	/**
	 * Loads the group information for a specified armor stand from its custom data and updates the group map accordingly.
	 *
	 * @param armorStand the armor stand to load the group information for
	 */
	public static void loadGroups(ArmorStand armorStand) {
		CustomData customData = armorStand.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
		CompoundTag customTag = customData.copyTag();
		List<String> groups = customTag.read("armor_poser_groups", Reference.TAG_LIST_CODEC).orElse(List.of());
		UUID uuid = armorStand.getUUID();
		for (String group : groups) {
			if (!isInGroup(group, uuid)) {
				addToGroupLocal(group, uuid);
			}
		}
	}

	/**
	 * Saves the group information for a specified armor stand to its custom data. If the group information has not changed, it will not update the custom data.
	 *
	 * @param armorStand the armor stand to save the group information for
	 * @param groups     the list of group names that the armor stand belongs to
	 */
	public static void saveGroups(ArmorStand armorStand, List<String> groups) {
		CustomData customData = armorStand.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
		CustomData changedData = customData.update((customTag) -> {
			List<String> oldGroups = customTag.read("armor_poser_groups", Reference.TAG_LIST_CODEC).orElse(new ArrayList<>());
			if (oldGroups.equals(groups)) {
				return;
			}
			customTag.store("armor_poser_groups", Reference.TAG_LIST_CODEC, groups);
		});
		if (!customData.equals(changedData)) {
			armorStand.setComponent(DataComponents.CUSTOM_DATA, changedData);
		}
	}

	/**
	 * Synchronizes the group information for all armor stands with the provided server group data. This will clear the existing group map and repopulate it based on the server data.
	 *
	 * @param serverGroups a map of armor stand UUIDs to lists of group names that they belong to, representing the current group information from the server
	 */
	public static void syncGroups(Map<UUID, List<String>> serverGroups) {
		groupMap.clear();
		for (Map.Entry<UUID, List<String>> entry : serverGroups.entrySet()) {
			for (String group : entry.getValue()) {
				groupMap.computeIfAbsent(group, _ -> new ArrayList<>()).add(entry.getKey());
			}
		}
	}
}
