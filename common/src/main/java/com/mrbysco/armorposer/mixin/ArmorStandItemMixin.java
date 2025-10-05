package com.mrbysco.armorposer.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.mrbysco.armorposer.util.PoseDefaults;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.item.ArmorStandItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ArmorStandItem.class)
public class ArmorStandItemMixin {

	@Inject(method = "useOn(Lnet/minecraft/world/item/context/UseOnContext;)Lnet/minecraft/world/InteractionResult;", at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/entity/decoration/ArmorStand;snapTo(DDDFF)V",
			shift = At.Shift.AFTER,
			ordinal = 0))
	public void armorposer$useOn(UseOnContext context, CallbackInfoReturnable<InteractionResult> cir,
	                             @Local ItemStack itemstack, @Local ArmorStand armorstand) {
		PoseDefaults.adjustArmorStand(itemstack, armorstand);
	}
}
