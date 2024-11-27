package com.mrbysco.armorposer.data;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Player;

import java.util.UUID;

public record RenameData(UUID entityUUID, String name) {
	public void encode(FriendlyByteBuf buf) {
		buf.writeUUID(entityUUID);
		buf.writeUtf(name);
	}

	public static RenameData decode(final FriendlyByteBuf packetBuffer) {
		return new RenameData(packetBuffer.readUUID(), packetBuffer.readUtf());
	}

	public void handleData(ArmorStand armorStand, Player player) {
		if (!name.isEmpty() && (player.experienceLevel >= 1 || player.getAbilities().instabuild)) {
			player.giveExperienceLevels(-1);
			armorStand.setCustomName(Component.literal(name));
		}
	}
}
