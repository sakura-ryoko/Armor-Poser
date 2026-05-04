package com.mrbysco.armorposer.data;

import com.mrbysco.armorposer.Reference;
import com.mrbysco.armorposer.client.GroupHelper;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Player;

import java.util.List;
import java.util.UUID;

public record GroupData(UUID entityUUID, List<String> groups) {
	public static final StreamCodec<FriendlyByteBuf, GroupData> STREAM_CODEC = StreamCodec.composite(
			UUIDUtil.STREAM_CODEC,
			o -> o.entityUUID,
			ByteBufCodecs.STRING_UTF8.apply(ByteBufCodecs.list()),
			o -> o.groups,
			GroupData::new
	);

	public void handleData(ArmorStand armorStand, Player player) {
		if (entityUUID == null) {
			Reference.LOGGER.warn("Received GroupData with null UUID from player {} - rejecting.", player.getName().getString());
			return;
		}
		if (groups == null) {
			return;
		}

		// Check uuid match
		if (!armorStand.getUUID().equals(entityUUID)) {
			Reference.LOGGER.warn("Player {} attempted to adjust groups on armor stand {} but UUID does not match ({}); rejecting packet.",
					player.getName().getString(),
					entityUUID, armorStand.getUUID());
			return;
		}

		// Dimension check
		var playerDim = player.level().dimension();
		var standDim = armorStand.level().dimension();
		if (!playerDim.equals(standDim)) {
			Reference.LOGGER.warn("Player {} attempted to adjust groups on armor stand {} from a different dimension (player dimension: {}, armor stand dimension: {}).",
					player.getName().getString(), entityUUID, player.level().dimension(), armorStand.level().dimension());
			return;
		}

		// Check distance
		final int maxDistance = Reference.getMaxDistance();
		final double maxDistanceSq = maxDistance * maxDistance;
		if (player.distanceToSqr(armorStand) > maxDistanceSq) {
			Reference.LOGGER.warn("Player {} attempted to adjust groups on armor stand {} from too far away (>{} blocks).",
					player.getName().getString(), entityUUID, maxDistance);
			return;
		}

		GroupHelper.saveGroups(armorStand, groups);
	}
}
