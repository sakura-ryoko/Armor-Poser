package com.mrbysco.armorposer.packets;

import com.mrbysco.armorposer.Reference;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

import java.util.List;

public record ArmorStandScreenPayload(int entityID, List<String> disabledFeatures) implements CustomPacketPayload {
	public static final StreamCodec<FriendlyByteBuf, ArmorStandScreenPayload> CODEC = StreamCodec.composite(
			ByteBufCodecs.INT,
			ArmorStandScreenPayload::entityID,
			ByteBufCodecs.STRING_UTF8.apply(ByteBufCodecs.list()),
			ArmorStandScreenPayload::disabledFeatures,
			ArmorStandScreenPayload::new);
	public static final Type<ArmorStandScreenPayload> ID = new Type<>(Reference.SCREEN_PACKET_ID);

	public ArmorStandScreenPayload(int entityID) {
		this(entityID, List.of());
	}

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return ID;
	}
}
