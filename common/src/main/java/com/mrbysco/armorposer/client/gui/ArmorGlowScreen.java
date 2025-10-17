package com.mrbysco.armorposer.client.gui;

import com.mrbysco.armorposer.client.GlowHandler;
import com.mrbysco.armorposer.client.gui.widgets.ArmorGlowWidget;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.decoration.ArmorStand;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ArmorGlowScreen extends Screen {
	private static final int PADDING = 6;

	private ArmorGlowWidget[] armorListWidget = new ArmorGlowWidget[2];
	private ArmorGlowWidget.ListEntry selected = null;

	private final List<ArmorStand> armorStands;
	private final List<ArmorStand> invisiblearmorStands;
	private Button locateButton;
	private Button modifyButton;

	public final ArmorStandScreen parentScreen;

	public ArmorGlowScreen(ArmorStandScreen parent) {
		super(Component.translatable("armorposer.gui.armor_list.list"));
		this.parentScreen = parent;

		this.minecraft = Minecraft.getInstance();

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
		this.armorStands = armorStands.stream().filter(stand -> !stand.isInvisible()).toList();
		this.invisiblearmorStands = armorStands.stream().filter(Entity::isInvisible).toList();
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
		addRenderableOnly(armorListWidget[0]);
		armorListWidget[0].refreshList(true);

		this.armorListWidget[1] = new ArmorGlowWidget(this, Component.translatable("armorposer.gui.armor_list.list2"),
				false, listWidth, fullButtonHeight, y - getScreenFont().lineHeight - PADDING);
		this.armorListWidget[1].setX(width - listWidth);
		this.armorListWidget[1].setY(16);
		this.armorListWidget[1].setHeight(this.height);
		addRenderableOnly(armorListWidget[1]);
		armorListWidget[1].refreshList(false);

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

	public <T extends ObjectSelectionList.Entry<T>> void buildVisiblePositions(Consumer<T> listViewConsumer, Function<ArmorStand, T> newEntry) {
		armorStands.forEach(stand -> listViewConsumer.accept(newEntry.apply(stand)));
	}

	public <T extends ObjectSelectionList.Entry<T>> void buildInvisiblePositions(Consumer<T> listViewConsumer, Function<ArmorStand, T> newEntry) {
		invisiblearmorStands.forEach(stand -> listViewConsumer.accept(newEntry.apply(stand)));
	}

	@Override
	public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
		super.render(guiGraphics, mouseX, mouseY, partialTicks);
	}

	@Override
	public boolean keyPressed(KeyEvent event) {
		if (this.armorListWidget[0].keyPressed(event)) {
			return true;
		} else if (this.armorListWidget[1].keyPressed(event)) {
			return true;
		}
		return super.keyPressed(event);
	}

	@Override
	public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
		//Nope
	}

	public void setSelected(ArmorGlowWidget.ListEntry previousEntry, ArmorGlowWidget.ListEntry entry, boolean visible) {
		if (this.selected == previousEntry) {
			this.selected = entry;
		} else {
			if (this.selected == null || entry != null) {
				this.selected = entry;
			}
		}
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
	public boolean mouseClicked(MouseButtonEvent buttonEvent, boolean flag) {
		boolean clicked = super.mouseClicked(buttonEvent, flag);
		if (this.armorListWidget[0].mouseClicked(buttonEvent, flag)) {
			return true;
		} else if (this.armorListWidget[1].mouseClicked(buttonEvent, flag)) {
			return true;
		}
		return clicked;
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
