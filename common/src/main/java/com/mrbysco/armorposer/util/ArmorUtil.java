package com.mrbysco.armorposer.util;

import net.minecraft.core.Rotations;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.decoration.ArmorStand;

public class ArmorUtil {

	public static CompoundTag writeAllPoses(ArmorStand armorStand) {
		CompoundTag compoundTag = new CompoundTag();

		compoundTag.store("Head", Rotations.CODEC, armorStand.getHeadPose());
		compoundTag.store("Body", Rotations.CODEC, armorStand.getBodyPose());
		compoundTag.store("LeftArm", Rotations.CODEC, armorStand.getLeftArmPose());
		compoundTag.store("RightArm", Rotations.CODEC, armorStand.getRightArmPose());
		compoundTag.store("LeftLeg", Rotations.CODEC, armorStand.getLeftLegPose());
		compoundTag.store("RightLeg", Rotations.CODEC, armorStand.getRightLegPose());

		return compoundTag;
	}
}
