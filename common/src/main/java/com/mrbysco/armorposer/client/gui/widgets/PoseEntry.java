package com.mrbysco.armorposer.client.gui.widgets;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mrbysco.armorposer.util.PoseData;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

public record PoseEntry(PoseData pose, boolean userAdded) implements Comparable<PoseEntry> {

	public PoseEntry(String name, String data, boolean userAdded) {
		this(new PoseData(name, data), userAdded);
	}

	public Component getName() {
		return userAdded() ? Component.literal(pose().name()) : Component.translatable("armorposer.gui.pose." + pose().name());
	}

	public CompoundTag getTag() {
		try {
			return TagParser.parseCompoundFully(pose().data());
		} catch (CommandSyntaxException e) {
			return null;
		}
	}

	@Override
	public int compareTo(@NotNull PoseEntry o) {
		return getName().getString().compareTo(o.getName().getString());
	}
}
