package com.mrbysco.armorposer.data;

import com.mrbysco.armorposer.Reference;
import net.minecraft.core.UUIDUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.TagValueOutput;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public record SyncData(UUID entityUUID, CompoundTag tag) {
	public static final StreamCodec<FriendlyByteBuf, SyncData> STREAM_CODEC = StreamCodec.composite(
			UUIDUtil.STREAM_CODEC,
			SyncData::entityUUID,
			ByteBufCodecs.COMPOUND_TAG,
			SyncData::tag,
			SyncData::new);
	private static final List<String> allowedKeys = List.of(
			"Invisible", "NoBasePlate", "NoGravity", "ShowArms", "Small", "CustomNameVisible", "Invulnerable",
			"DisabledSlots", "Pose", "Scale", "Move", "Rotation"
	);
	private static final Map<String, String> permissionKeyMap = Map.of(
			"Invisible", "invisible",
			"NoBasePlate", "base_plate",
			"NoGravity", "gravity",
			"ShowArms", "show_arms",
			"Small", "small",
			"CustomNameVisible", "name_visible",
			"Rotation", "rotation",
			"Scale", "resize"
	);

	public void handleData(ArmorStand armorStand, Player player) {
		// Create a new TagValueOutput to save the armor stand's data
		try (ProblemReporter.ScopedCollector problemreporter$scopedcollector = new ProblemReporter.ScopedCollector(Reference.LOGGER)) {
			TagValueOutput output = TagValueOutput.createWithContext(problemreporter$scopedcollector, armorStand.registryAccess());
			// Save the armor stand's current state without an ID
			armorStand.saveWithoutId(output);
			// Build the result compound tag from the output
			CompoundTag outputCompound = output.buildResult();

			if (!tag.isEmpty()) {
				List<String> keysToRemove = tag.keySet().stream()
						.filter(key -> !allowedKeys.contains(key))
						.toList();
				keysToRemove.forEach(tag::remove);

				outputCompound.merge(tag);
				armorStand.load(TagValueInput.create(ProblemReporter.DISCARDING, armorStand.registryAccess(), outputCompound));
				armorStand.setUUID(entityUUID);

				// Permission checks
				for (Map.Entry<String, String> entry : permissionKeyMap.entrySet()) {
					String nbtKey = entry.getKey();
					String permissionKey = entry.getValue();
					if (tag.contains(nbtKey) && !Reference.canUseFeature(player, permissionKey)) {
						tag.remove(nbtKey);
					}
				}

				Vec3 offset = tag.read("Move", Vec3.CODEC).orElse(Vec3.ZERO);
				double xOffset = offset.x();
				double yOffset = offset.y();
				double zOffset = offset.z();
				if (xOffset != 0 || yOffset != 0 || zOffset != 0)
					armorStand.setPosRaw(armorStand.getX() + xOffset,
							armorStand.getY() + yOffset,
							armorStand.getZ() + zOffset);

				double scale = tag.getDoubleOr("Scale", 0);
				if (scale > 0) {
					AttributeInstance attributeInstance = armorStand.getAttributes().getInstance(Attributes.SCALE);
					if (attributeInstance != null) {
						attributeInstance.setBaseValue(scale);
					}
				}
			}
		}
	}
}
