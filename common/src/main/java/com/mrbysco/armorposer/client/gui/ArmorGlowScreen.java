package com.mrbysco.armorposer.client.gui;

import com.mrbysco.armorposer.Reference;
import com.mrbysco.armorposer.client.GlowHandler;
import com.mrbysco.armorposer.client.gui.widgets.ArmorGlowWidget;
import com.mrbysco.armorposer.client.gui.widgets.PoseListWidget;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.level.Level;
import org.joml.Quaternionf;

import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ArmorGlowScreen extends Screen {
	private static final int PADDING = 6;

	private ArmorGlowWidget[] armorListWidget = new ArmorGlowWidget[2];
	private ArmorGlowWidget.ListEntry selected = null;
	private final List<ArmorStand> armorStands;
	private Button locateButton;
	private Button modifyButton;

	public final ArmorStandScreen parentScreen;

	private final ArmorStand exampleArmorStand;

	public ArmorGlowScreen(ArmorStandScreen parent) {
		super(Component.translatable("armorposer.gui.armor_list.list"));
		this.parentScreen = parent;

		this.minecraft = Minecraft.getInstance();
		Level level = this.minecraft.level;
		if (level != null) {
			this.exampleArmorStand = new ArmorStand(level, 0, 0, 0);
			this.exampleArmorStand.setShowArms(true);
			this.exampleArmorStand.yBodyRot = 210.0F;
			this.exampleArmorStand.setXRot(25.0F);
			this.exampleArmorStand.yHeadRot = exampleArmorStand.getYRot();
			this.exampleArmorStand.yHeadRotO = exampleArmorStand.getYRot();
		} else {
			this.exampleArmorStand = null;
		}

		//Add the armor stands to the list
		if (minecraft.player == null)
			this.onClose();

		List<ArmorStand> armorStands = minecraft.level.getEntitiesOfClass(ArmorStand.class,
				minecraft.player.getBoundingBox().inflate(30.0D), EntitySelector.LIVING_ENTITY_STILL_ALIVE).stream().collect(Collectors.toList());
		//Sort the list based on how far the armor stand is from the player
		armorStands.sort((armorStand, armorStand2) -> {
			double distance1 = armorStand.distanceToSqr(minecraft.player);
			double distance2 = armorStand2.distanceToSqr(minecraft.player);
			return Double.compare(distance1, distance2);
		});
		this.armorStands = Collections.unmodifiableList(armorStands);
	}

	@Override
	public boolean isPauseScreen() {
		return false;
	}

	@Override
	protected void init() {
		int centerWidth = this.width / 2;
		int listWidth = this.width / 4 + 20;
		int structureWidth = this.width - listWidth - (PADDING * 3);
		int closeButtonWidth = Math.min(structureWidth, 160);
		int y = this.height - 20 - PADDING;
		this.addRenderableWidget(Button.builder(Component.translatable("gui.cancel"), b -> ArmorGlowScreen.this.onClose())
				.bounds(centerWidth - (closeButtonWidth / 2) + PADDING, y, closeButtonWidth, 20).build());

		y -= 18 + PADDING;
		int buttonWidth = (closeButtonWidth / 2) - 1;
		this.addRenderableWidget(this.locateButton = Button.builder(Component.translatable("armorposer.gui.armor_list.locate"), b -> {
			if (selected != null && minecraft.player != null) {
				GlowHandler.startGlowing(this.selected.getArmorStand().getUUID());
				minecraft.player.lookAt(EntityAnchorArgument.Anchor.EYES, selected.getArmorStand().position());
			}
		}).bounds(centerWidth - (closeButtonWidth / 2) + PADDING, y, buttonWidth, 20).build());
		this.addRenderableWidget(this.modifyButton = Button.builder(Component.translatable("armorposer.gui.armor_list.modify"), b -> {
			if (selected != null && minecraft.player != null) {
				minecraft.setScreen(new ArmorStandScreen(selected.getArmorStand()));
			}
		}).bounds(centerWidth - (closeButtonWidth / 2) + PADDING + buttonWidth + 2, y, buttonWidth, 20).build());


		int fullButtonHeight = PADDING + 20 + PADDING;
		this.armorListWidget[0] = new ArmorGlowWidget(this, Component.translatable("armorposer.gui.armor_list.list"),
				true, listWidth, fullButtonHeight, y - getScreenFont().lineHeight - PADDING);
		this.armorListWidget[0].setX(0);
		this.armorListWidget[0].setY(16);
		this.armorListWidget[0].setHeight(this.height);

		this.armorListWidget[1] = new ArmorGlowWidget(this, Component.translatable("armorposer.gui.armor_list.list2"),
				false, listWidth, fullButtonHeight, y - getScreenFont().lineHeight - PADDING);
		this.armorListWidget[1].setX(width - listWidth);
		this.armorListWidget[1].setY(16);
		this.armorListWidget[1].setHeight(this.height);

		addWidget(armorListWidget[0]);
		addWidget(armorListWidget[1]);

		updateCache();
	}

	@Override
	public void tick() {
		if (armorListWidget[0].children().contains(selected)) {
			armorListWidget[0].setSelected(selected);
			armorListWidget[1].setSelected(null);
		} else if (armorListWidget[1].children().contains(selected)) {
			armorListWidget[0].setSelected(null);
			armorListWidget[1].setSelected(selected);
		}
	}

	public <T extends ObjectSelectionList.Entry<T>> void buildPositionList(Consumer<T> ListViewConsumer, Function<ArmorStand, T> newEntry, boolean visible) {
		List<ArmorStand> filteredArmorStands = this.armorStands.stream()
				.filter(armorStand -> visible == !armorStand.isInvisible())
				.toList();
		filteredArmorStands.forEach(stand -> ListViewConsumer.accept(newEntry.apply(stand)));
	}

	@Override
	public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
		this.armorListWidget[0].render(guiGraphics, mouseX, mouseY, partialTicks);
		this.armorListWidget[1].render(guiGraphics, mouseX, mouseY, partialTicks);

		ArmorStand selectedArmorStand = this.selected != null ? this.selected.getArmorStand() : this.exampleArmorStand;
		if (selectedArmorStand != null) {
			renderEntity(guiGraphics, selectedArmorStand, this.width / 2, 100);
		}
		super.render(guiGraphics, mouseX, mouseY, partialTicks);
	}

	private void renderEntity(GuiGraphics guiGraphics, LivingEntity entity, int xPos, int yPos) {
		int startX = xPos - 80;
		int startY = yPos - 120;
		int endX = xPos + 80;
		int endY = yPos + 120;
		InventoryScreen.renderEntityInInventory(guiGraphics, startX, startY, endX, endY,
				50.0F, Reference.ARMOR_STAND_TRANSLATION, Reference.ARMOR_STAND_ANGLE, (Quaternionf) null, entity);
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		return super.keyPressed(keyCode, scanCode, modifiers);
	}

	@Override
	public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
		//Nope
	}

	public void setSelected(ArmorGlowWidget.ListEntry entry) {
		this.selected = entry == this.selected ? null : entry;
		updateCache();
	}

	private void updateCache() {
		this.locateButton.active = selected != null;
		this.modifyButton.active = selected != null;
	}

	/**
	 * Clear the search field when right-clicked on it
	 */
	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		return super.mouseClicked(mouseX, mouseY, button);
	}

	@Override
	public void resize(Minecraft mc, int width, int height) {
		ArmorGlowWidget.ListEntry selected = this.selected;
		this.init(mc, width, height);
		this.selected = selected;
		updateCache();
	}

	@Override
	public void onClose() {
		this.minecraft.setScreen(parentScreen);
	}

	public Minecraft getScreenMinecraft() {
		return this.minecraft;
	}

	public Font getScreenFont() {
		return this.font;
	}
}
