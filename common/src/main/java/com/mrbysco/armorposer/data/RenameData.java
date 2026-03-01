package com.mrbysco.armorposer.data;

import com.mrbysco.armorposer.Reference;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.StringUtil;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Player;

import java.util.UUID;

public record RenameData(UUID entityUUID, String name) {
	public static final StreamCodec<FriendlyByteBuf, RenameData> STREAM_CODEC = StreamCodec.composite(
			UUIDUtil.STREAM_CODEC,
			RenameData::entityUUID,
			ByteBufCodecs.stringUtf8(50),
			RenameData::name,
			RenameData::new);

	private static final int MAX_NAME_LENGTH = 50;

	public void handleData(ArmorStand armorStand, Player player) {
		if (entityUUID == null) {
			Reference.LOGGER.warn("Received RenameData with null UUID from player {} - rejecting.", player.getName().getString());
			return;
		}
		if (name == null) {
			return;
		}

		// Check uuid match
		if (!armorStand.getUUID().equals(entityUUID)) {
			Reference.LOGGER.warn("Player {} attempted to rename armor stand {} but UUID does not match ({}); rejecting packet.",
					player.getName().getString(),
					entityUUID, armorStand.getUUID());
			return;
		}

		// Dimension check
		var playerDim = player.level().dimension();
		var standDim = armorStand.level().dimension();
		if (!playerDim.equals(standDim)) {
			Reference.LOGGER.warn("Player {} attempted to rename armor stand {} from a different dimension (player dimension: {}, armor stand dimension: {}).",
					player.getName().getString(), entityUUID, player.level().dimension(), armorStand.level().dimension());
			return;
		}

		// Check distance
		final int maxDistance = Reference.getMaxDistance();
		final double maxDistanceSq = maxDistance * maxDistance;
		if (player.distanceToSqr(armorStand) > maxDistanceSq) {
			Reference.LOGGER.warn("Player {} attempted to sync armor stand {} from too far away (>{} blocks).",
					player.getName().getString(), entityUUID, maxDistance);
			return;
		}

		// Filter text for inappropriate characters
		String filteredText = StringUtil.filterText(name);

		// Check name length
		if (filteredText.length() > MAX_NAME_LENGTH) {
			Reference.LOGGER.warn("Player {} attempted to rename armor stand {} with a name that is too long ({} characters, max {}).",
					player.getName().getString(), entityUUID, filteredText.length(), MAX_NAME_LENGTH);
			return;
		}

		if (!filteredText.isEmpty() && (player.experienceLevel >= 1 || player.getAbilities().instabuild)) {
			if (!player.getAbilities().instabuild) {
				player.giveExperienceLevels(-1);
			}
			armorStand.setCustomName(Component.literal(filteredText));
		}
	}
}
