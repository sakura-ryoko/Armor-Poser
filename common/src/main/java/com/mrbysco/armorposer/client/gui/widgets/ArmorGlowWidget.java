package com.mrbysco.armorposer.client.gui.widgets;

import com.mrbysco.armorposer.Reference;
import com.mrbysco.armorposer.client.gui.ArmorGlowScreen;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.renderer.entity.state.ArmorStandRenderState;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.ARGB;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.decoration.ArmorStand;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3x2fStack;

public class ArmorGlowWidget extends ObjectSelectionList<ArmorGlowWidget.ListEntry> {
	private final ArmorGlowScreen parent;
	private final int listWidth;
	private final Component title;

	public ArmorGlowWidget(ArmorGlowScreen parent, Component title, boolean visible, int listWidth, int top, int bottom) {
		super(parent.getScreenMinecraft(), listWidth, bottom - top, top, parent.getScreenFont().lineHeight * 2 + 16);
		this.parent = parent;
		this.title = title;
		this.listWidth = listWidth;
		this.refreshList(visible);
	}

	@Override
	protected int scrollBarX() {
		return this.listWidth;
	}

	@Override
	public int getRowWidth() {
		return this.listWidth;
	}

	public void refreshList(boolean visible) {
		this.clearEntries();
		if (visible)
			parent.buildVisiblePositions(this::addEntry, location -> new ArmorGlowWidget.ListEntry(location, this.parent));
		else
			parent.buildInvisiblePositions(this::addEntry, location -> new ArmorGlowWidget.ListEntry(location, this.parent));
	}

	@Override
	protected void renderSelection(GuiGraphics guiGraphics, ArmorGlowWidget.ListEntry listEntry, int p_240142_) {
		int x1 = listEntry.getX();
		int y1 = listEntry.getY();
		int x2 = x1 + listEntry.getWidth();
		int y2 = y1 + listEntry.getHeight();
		guiGraphics.fillGradient(x1, y1, x2, y2, -1945083888, -1676648432);
	}

	@Override
	public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
		guiGraphics.fillGradient(getX(), 0, getX() + this.listWidth, parent.height, -1945104368, -1676668912);
		super.renderWidget(guiGraphics, mouseX, mouseY, partialTicks);
		guiGraphics.drawCenteredString(this.parent.getScreenFont(), title, getX() + this.listWidth / 2, 2, ARGB.opaque(16777215));
	}

	@Override
	public void setSelected(@Nullable ArmorGlowWidget.ListEntry selected) {
		this.parent.setSelected(getSelected(), selected, visible);
		super.setSelected(selected);
	}

	public class ListEntry extends Entry<ListEntry> {
		private final ArmorGlowScreen parent;
		private final ArmorStandRenderState armorStandPreview = new ArmorStandRenderState();
		private final ArmorStand armorstand;
		private final float scale;
		private final boolean showPlate;
		private final boolean locked;

		ListEntry(ArmorStand armorStand, ArmorGlowScreen parent) {
			this.parent = parent;
			this.scale = armorStand.getScale();
			this.showPlate = armorStand.showBasePlate();
			this.locked = armorStand.isInvulnerable();

			this.armorstand = armorStand;

			this.armorStandPreview.entityType = EntityType.ARMOR_STAND;
			this.armorStandPreview.xRot = 25.0F;
			this.armorStandPreview.bodyRot = 210.0F;
			this.updateState(armorStand);
		}

		public void updateState(ArmorStand stand) {
			this.armorStandPreview.showBasePlate = stand.showBasePlate();
			this.armorStandPreview.isSmall = stand.isSmall();
			this.armorStandPreview.showArms = stand.showArms();
			this.armorStandPreview.isInvisible = stand.isInvisible();
			this.armorStandPreview.nameTag = stand.getCustomName();

			this.armorStandPreview.headPose = stand.getHeadPose();
			this.armorStandPreview.bodyPose = stand.getBodyPose();
			this.armorStandPreview.leftArmPose = stand.getLeftArmPose();
			this.armorStandPreview.rightArmPose = stand.getRightArmPose();
			this.armorStandPreview.leftLegPose = stand.getLeftLegPose();
			this.armorStandPreview.rightLegPose = stand.getRightLegPose();
		}

		@Override
		public void renderContent(GuiGraphics guiGraphics, int mouseX, int mouseY, boolean hovered, float partialTick) {
			int left = getContentX();
			int top = getContentY();
			Matrix3x2fStack pose = guiGraphics.pose();
			pose.pushMatrix();
			pose.translate(0, top - (height / 2));
			renderScrollingStringOverContents(guiGraphics.textRenderer(), getPositionComponent(), 18);

			if (isMouseOver(mouseX, mouseY)) {
				Font font = this.parent.getScreenFont();
				Component component = Component.translatable("armorposer.gui.armor_list.stats", scale);
				guiGraphics.setTooltipForNextFrame(font, component, mouseX, mouseY);
			}
			if (isVisible() && getSelected() == this)
				renderPose(guiGraphics, left + 16, top + 28, partialTick);
			pose.popMatrix();
		}

		public ArmorStand getArmorStand() {
			return armorstand;
		}

		public boolean isLocked() {
			return locked;
		}

		public boolean isVisible() {
			return !armorStandPreview.isInvisible;
		}

		public void renderPose(GuiGraphics guiGraphics, int xPos, int yPos, float partialTick) {
			if (armorStandPreview != null) {
				int startX = xPos - 40;
				int startY = yPos - 60;
				int endX = xPos + 40;
				int endY = yPos + 60;

				guiGraphics.submitEntityRenderState(this.armorStandPreview, 20.0F,
						Reference.ARMOR_STAND_TRANSLATION, Reference.ARMOR_STAND_ANGLE, null, startX, startY, endX, endY);
			}
		}

		public Component getPositionComponent() {
			MutableComponent component = Component.literal(this.getArmorStand().blockPosition().toShortString());
			if (this.showPlate)
				component = component.withStyle(ChatFormatting.UNDERLINE);
			if (this.isLocked())
				component = component.append(" \uD83D\uDD12").withStyle(ChatFormatting.BOLD);
			return component;
		}

		@Override
		public Component getNarration() {
			return getPositionComponent();
		}
	}
}
