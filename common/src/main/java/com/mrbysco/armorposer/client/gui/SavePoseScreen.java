package com.mrbysco.armorposer.client.gui;

import com.mrbysco.armorposer.Reference;
import com.mrbysco.armorposer.client.gui.widgets.ToggleButton;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import net.minecraft.util.ARGB;
import net.minecraft.world.phys.Vec3;

public class SavePoseScreen extends Screen {
	private final ArmorStandScreen parentScreen;

	private Button saveButton;
	private EditBox nameField;
	private boolean hasOffset;
	private boolean hasActions;
	private ToggleButton offsetToggle;
	private ToggleButton actionsToggle;

	public SavePoseScreen(ArmorStandScreen armorStandScreen) {
		super(Component.translatable("armorposer.gui.save_pose.title"));
		this.parentScreen = armorStandScreen;
	}

	protected void updateState() {
		Vec3 currentOffset = this.parentScreen.getOffset();
		this.hasOffset = currentOffset != null && (currentOffset.x() != 0.0 || currentOffset.y() != 0.0 || currentOffset.z() != 0.0);

		ListTag actions = this.parentScreen.getRecordedActions();
		this.hasActions = !(actions == null || actions.isEmpty());
	}

	@Override
	protected void init() {
		this.addRenderableWidget(this.offsetToggle = new ToggleButton.Builder(false, (button) -> {
			ToggleButton toggleButton = ((ToggleButton) button);
			toggleButton.setValue(!toggleButton.getValue());
		}).bounds(this.width / 2 + 30, this.height / 2 - 72, 40, 20).build());
		this.offsetToggle.setTooltip(Tooltip.create(Component.translatable("armorposer.gui.tooltip.include_offset")));
		if (!hasOffset) {
			this.offsetToggle.active = false;
		}

		this.addRenderableWidget(this.actionsToggle = new ToggleButton.Builder(false, (button) -> {
			ToggleButton toggleButton = ((ToggleButton) button);
			toggleButton.setValue(!toggleButton.getValue());
		}).bounds(this.width / 2 + 30, this.height / 2 - 50, 40, 20).build());
		this.actionsToggle.setTooltip(Tooltip.create(Component.translatable("armorposer.gui.tooltip.include_actions")));
		if (!hasActions) {
			this.actionsToggle.active = false;
		}

		this.addRenderableWidget(this.saveButton = Button.builder(Component.translatable("armorposer.gui.label.save"), (button) -> {
			CompoundTag compound = this.parentScreen.writeFieldsToNBT();

			if (offsetToggle.getValue()) {
				compound.store("Move", Vec3.CODEC, this.parentScreen.getOffset());
			}

			if (actionsToggle.getValue()) {
				ListTag recorded = this.parentScreen.getRecordedActions();
				if (!recorded.isEmpty()) {
					ListTag copy = new ListTag();
					for (int i = 0; i < recorded.size(); i++) {
						copy.add(recorded.getCompoundOrEmpty(i).copy());
					}
					compound.put("Actions", copy);
				}
			}
			Reference.savePose(this.nameField.getValue(), compound);
			this.minecraft.setScreenAndShow(this.parentScreen);
		}).bounds(this.width / 2 - 66, this.height / 2 + 3, 60, 20).build());

		this.addRenderableWidget(Button.builder(CommonComponents.GUI_CANCEL, (button) -> {
			this.minecraft.setScreenAndShow(this.parentScreen);
		}).bounds(this.width / 2 - 4, this.height / 2 + 3, 60, 20).build());

		this.nameField = new EditBox(this.font, this.width / 2 - 90, this.height / 2 - 24, 180, 20, Component.literal("Name"));
		this.nameField.setMaxLength(31);
		this.nameField.setTextColor(-1);
		this.addWidget(this.nameField);
		setInitialFocus(nameField);
	}

	@Override
	public void tick() {
		super.tick();

		this.saveButton.active = !this.nameField.getValue().isEmpty();
	}

	@Override
	public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTicks) {
		super.extractRenderState(graphics, mouseX, mouseY, partialTicks);

		graphics.centeredText(this.font, this.title, this.width / 2, 20, ARGB.opaque(16777215));
		graphics.text(font, Component.translatable("armorposer.gui.save_pose.include_offset"), this.width / 2 - 60, this.height / 2 - 66,
				hasOffset ? ARGB.opaque(16777215) : ARGB.opaque(TextColor.GRAY.getValue()), false);
		graphics.text(font, Component.translatable("armorposer.gui.save_pose.include_actions"), this.width / 2 - 60, this.height / 2 - 46,
				hasActions ? ARGB.opaque(16777215) : ARGB.opaque(TextColor.GRAY.getValue()), false);

		this.nameField.extractRenderState(graphics, mouseX, mouseY, partialTicks);
	}

	@Override
	public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTicks) {
		super.extractTransparentBackground(graphics);
	}
}
