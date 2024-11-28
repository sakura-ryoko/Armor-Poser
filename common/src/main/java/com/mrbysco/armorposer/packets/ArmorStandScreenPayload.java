package com.mrbysco.armorposer.packets;

import com.mrbysco.armorposer.Reference;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record ArmorStandScreenPayload(int entityID) implements CustomPacketPayload {
	public static final StreamCodec<FriendlyByteBuf, ArmorStandScreenPayload> CODEC = StreamCodec.composite(
			ByteBufCodecs.INT,
			ArmorStandScreenPayload::entityID,
			ArmorStandScreenPayload::new);
	public static final Type<ArmorStandScreenPayload> ID = new Type<>(Reference.SCREEN_PACKET_ID);

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return ID;
	}
}
