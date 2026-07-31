package com.mrbysco.armorposer.data;

import com.mrbysco.armorposer.Reference;
import com.mrbysco.armorposer.config.PoserConfig;
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
			"Scale", "resize",
			"Move", "position"
	);
	private static final double MAX_MOVE_OFFSET = 8.0D;

	public void handleData(ArmorStand armorStand, Player player) {
		if (entityUUID == null || tag == null) {
			Reference.LOGGER.warn("Received SyncData with null fields from player {} - rejecting.", player.getName().getString());
			return;
		}

		// Check uuid match
		if (!armorStand.getUUID().equals(entityUUID)) {
			Reference.LOGGER.warn("Player {} attempted to sync armor stand with mismatched UUID: {} (armor stand UUID: {}).",
					player.getName().getString(),
					entityUUID, armorStand.getUUID());
			return;
		}

		// Dimension check
		var playerDim = player.level().dimension();
		var standDim = armorStand.level().dimension();
		if (!playerDim.equals(standDim)) {
			Reference.LOGGER.warn("Player {} attempted to sync armor stand {} from a different dimension (player dimension: {}, armor stand dimension: {}).",
					player.getName().getString(), entityUUID, player.level().dimension(), armorStand.level().dimension());
			return;
		}

		// Check distance
		final int maxDistance = Reference.getMaxDistance();
		final double maxDistanceSq = maxDistance * maxDistance;
		if (player.distanceToSqr(armorStand) > maxDistanceSq) {
			Reference.LOGGER.warn("Player {} attempted to sync armor stand {} that is too far away (>{} blocks).",
					player.getName().getString(), entityUUID, maxDistance);
			return;
		}

		// Reject empty tags
		if (tag.isEmpty()) {
			return;
		}

		// Remove any keys that aren't in the allowed list
		List<String> keysToRemove = tag.keySet().stream()
				.filter(key -> !allowedKeys.contains(key))
				.toList();
		keysToRemove.forEach(tag::remove);

		// Permission checks
		for (Map.Entry<String, String> entry : permissionKeyMap.entrySet()) {
			String nbtKey = entry.getKey();
			String permissionKey = entry.getValue();
			if (tag.contains(nbtKey) && !Reference.canUseFeature(player, permissionKey)) {
				tag.remove(nbtKey);
			}
		}

		if (tag.contains("Move")) {
			try {
				Vec3 offset = tag.read("Move", Vec3.CODEC).orElse(Vec3.ZERO);
				double x = offset.x();
				double y = offset.y();
				double z = offset.z();
				boolean finite = Double.isFinite(x) && Double.isFinite(y) && Double.isFinite(z);
				boolean withinBounds = Math.abs(x) <= MAX_MOVE_OFFSET && Math.abs(y) <= MAX_MOVE_OFFSET && Math.abs(z) <= MAX_MOVE_OFFSET;
				if (!finite) {
					Reference.LOGGER.warn("Rejecting Move from SyncData for {} because offset contains non-finite values: {}", entityUUID, offset);
					tag.remove("Move");
				} else if (!withinBounds) {
					Reference.LOGGER.warn("Rejecting Move from SyncData for {} because offset exceeds max bounds (>{}): {}", entityUUID, MAX_MOVE_OFFSET, offset);
					tag.remove("Move");
				}
			} catch (Exception e) {
				Reference.LOGGER.warn("Failed to decode Move from SyncData for {}: {}; removing Move key.", entityUUID, e.toString());
				tag.remove("Move");
			}
		}

		if (tag.contains("Scale")) {
			double scale;
			try {
				scale = tag.getDoubleOr("Scale", 0.0);
			} catch (Exception e) {
				Reference.LOGGER.warn("Invalid Scale value in SyncData for {}: {}; removing Scale key.", entityUUID, e.toString());
				tag.remove("Scale");
				scale = Double.NaN;
			}

			if (tag.contains("Scale")) { // still present after possible removal
				double minScale = PoserConfig.COMMON.minScale.getAsDouble();
				double maxScale = PoserConfig.COMMON.maxScale.getAsDouble();
				if (!Double.isFinite(scale) || scale < minScale || scale > maxScale) {
					Reference.LOGGER.warn("Ignoring invalid Scale {} from player {} for armor stand {}.", scale, player.getName().getString(), entityUUID);
					tag.remove("Scale");
				}
			}
		}

		// Create a new TagValueOutput to save the armor stand's data
		try (ProblemReporter.ScopedCollector problemreporter$scopedcollector = new ProblemReporter.ScopedCollector(Reference.LOGGER)) {
			TagValueOutput output = TagValueOutput.createWithContext(problemreporter$scopedcollector, armorStand.registryAccess());
			// Save the armor stand's current state without an ID
			armorStand.saveWithoutId(output);
			// Build the result compound tag from the output
			CompoundTag outputCompound = output.buildResult();

			if (!tag.isEmpty()) {
				outputCompound.merge(tag);
				armorStand.load(TagValueInput.create(ProblemReporter.DISCARDING, armorStand.registryAccess(), outputCompound));
				armorStand.setUUID(entityUUID);

				Vec3 offset = tag.read("Move", Vec3.CODEC).orElse(Vec3.ZERO);
				double xOffset = offset.x();
				double yOffset = offset.y();
				double zOffset = offset.z();

				if (xOffset != 0.0 || yOffset != 0.0 || zOffset != 0.0) {
					armorStand.setPosRaw(armorStand.getX() + xOffset,
							armorStand.getY() + yOffset,
							armorStand.getZ() + zOffset);
				}

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
