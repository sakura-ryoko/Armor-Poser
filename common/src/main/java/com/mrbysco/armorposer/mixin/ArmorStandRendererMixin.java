package com.mrbysco.armorposer.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.mrbysco.armorposer.util.NameTagHelper;
import net.minecraft.client.model.object.armorstand.ArmorStandArmorModel;
import net.minecraft.client.renderer.entity.ArmorStandRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.state.ArmorStandRenderState;
import net.minecraft.world.entity.decoration.ArmorStand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ArmorStandRenderer.class)
public abstract class ArmorStandRendererMixin extends LivingEntityRenderer<ArmorStand, ArmorStandRenderState, ArmorStandArmorModel> {
	public ArmorStandRendererMixin(EntityRendererProvider.Context context, ArmorStandArmorModel model, float shadowRadius) {
		super(context, model, shadowRadius);
	}

	@ModifyReturnValue(method = "shouldShowName(Lnet/minecraft/world/entity/decoration/ArmorStand;D)Z", at = @At("RETURN"))
	private boolean adjustHasLabel(boolean original, ArmorStand entity, double distanceToCameraSq) {
		if (!original) {
			return false;
		}

		return NameTagHelper.canRenderNameTag(this.entityRenderDispatcher, entity, distanceToCameraSq);
	}
}
