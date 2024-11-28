package com.mrbysco.armorposer.data;

import net.minecraft.core.UUIDUtil;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Player;

import java.util.UUID;

public record RenameData(UUID entityUUID, String name) {
	public static final StreamCodec<FriendlyByteBuf, RenameData> STREAM_CODEC = StreamCodec.composite(
			UUIDUtil.STREAM_CODEC,
			RenameData::entityUUID,
			ByteBufCodecs.STRING_UTF8,
			RenameData::name,
			RenameData::new);

	public void handleData(ArmorStand armorStand, Player player) {
		if (!name.isEmpty() && (player.experienceLevel >= 1 || player.getAbilities().instabuild)) {
			player.giveExperienceLevels(-1);
			armorStand.setCustomName(Component.literal(name));
		}
	}
}
