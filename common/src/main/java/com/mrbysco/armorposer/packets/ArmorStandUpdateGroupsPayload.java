package com.mrbysco.armorposer.packets;

import com.mrbysco.armorposer.Reference;
import com.mrbysco.armorposer.data.GroupData;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;


public record ArmorStandUpdateGroupsPayload(GroupData data) implements CustomPacketPayload {
	public static final StreamCodec<FriendlyByteBuf, ArmorStandUpdateGroupsPayload> CODEC = CustomPacketPayload.codec(
			ArmorStandUpdateGroupsPayload::write,
			ArmorStandUpdateGroupsPayload::new);
	public static final Type<ArmorStandUpdateGroupsPayload> ID = new Type<>(Reference.UPDATE_GROUP_PACKET_ID);

	public ArmorStandUpdateGroupsPayload(final FriendlyByteBuf packetBuffer) {
		this(GroupData.STREAM_CODEC.decode(packetBuffer));
	}

	public void write(FriendlyByteBuf buf) {
		GroupData.STREAM_CODEC.encode(buf, data());
	}

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return ID;
	}
}
