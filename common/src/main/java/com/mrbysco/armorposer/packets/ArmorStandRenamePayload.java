package com.mrbysco.armorposer.packets;

import com.mrbysco.armorposer.Reference;
import com.mrbysco.armorposer.data.RenameData;
import com.mrbysco.armorposer.data.SyncData;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;


public record ArmorStandRenamePayload(RenameData data) implements CustomPacketPayload {
	public static final StreamCodec<FriendlyByteBuf, ArmorStandRenamePayload> CODEC = CustomPacketPayload.codec(
			ArmorStandRenamePayload::write,
			ArmorStandRenamePayload::new);
	public static final Type<ArmorStandRenamePayload> ID = new Type<>(Reference.RENAME_PACKET_ID);

	public ArmorStandRenamePayload(final FriendlyByteBuf packetBuffer) {
		this(RenameData.decode(packetBuffer));
	}

	public void write(FriendlyByteBuf buf) {
		data.encode(buf);
	}

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return ID;
	}
}
