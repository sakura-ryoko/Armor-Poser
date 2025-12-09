package com.mrbysco.armorposer.client.gui.widgets;

import com.mojang.blaze3d.platform.cursor.CursorTypes;
import com.mrbysco.armorposer.mixin.AbstractSliderButtonAccessor;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;

import java.text.DecimalFormat;

public class RangeSlider extends AbstractSliderButton {
	protected Component prefix;
	protected Component suffix;

	protected double minValue;
	protected double maxValue;

	private final double stepSize = 1.0D;
	private final DecimalFormat format = new DecimalFormat(Double.toString(this.stepSize).replaceAll("\\d", "0"));

	protected boolean drawString;

	protected final RangeSlider.OnChange onChange;

	public RangeSlider(int x, int y, int width, int height, Component prefix,
	                   Component suffix, double minValue, double maxValue, double currentValue,
	                   boolean drawString, OnChange onChange) {
		super(x, y, width, height, Component.empty(), 0D);
		this.prefix = prefix;
		this.suffix = suffix;
		this.minValue = minValue;
		this.maxValue = maxValue;
		this.value = this.snapToNearest((currentValue - minValue) / (maxValue - minValue));
		this.drawString = drawString;


		this.onChange = onChange;
		this.updateMessage();
	}

	public double getValue() {
		return this.value * (maxValue - minValue) + minValue;
	}

	public long getValueLong() {
		return Math.round(this.getValue());
	}

	public int getValueInt() {
		return (int) this.getValueLong();
	}

	public void setValue(double value) {
		setFractionalValue((value - this.minValue) / (this.maxValue - this.minValue));
	}

	public String getValueString() {
		return this.format.format(this.getValue());
	}

	@Override
	public void onClick(MouseButtonEvent event, boolean doubleClick) {
		((AbstractSliderButtonAccessor) this).armorposer$setDragging(this.active);
		this.setValueFromMouse(event.x());
	}

	@Override
	protected void onDrag(MouseButtonEvent event, double dragX, double dragY) {
		super.onDrag(event, dragX, dragY);
		this.setValueFromMouse(event.x());
	}

	@Override
	public boolean keyPressed(KeyEvent keyEvent) {
		boolean flag = keyEvent.isLeft();
		if (flag || keyEvent.isRight()) {
			if (this.minValue > this.maxValue)
				flag = !flag;
			float f = flag ? -1F : 1F;
			this.setValue(this.getValue() + f * this.stepSize);
		}

		return false;
	}

	private void setValueFromMouse(double mouseX) {
		this.setFractionalValue((mouseX - (this.getX() + 4)) / (this.width - 8));
	}

	/**
	 * @param fractionalValue fractional progress between 0 and 1
	 */
	private void setFractionalValue(double fractionalValue) {
		double oldValue = this.value;
		this.value = this.snapToNearest(fractionalValue);
		if (!Mth.equal(oldValue, this.value))
			this.applyValue();

		this.updateMessage();
	}

	private double snapToNearest(double value) {
		value = Mth.lerp(Mth.clamp(value, 0D, 1D), this.minValue, this.maxValue);

		value = (stepSize * Math.round(value / stepSize));

		if (this.minValue > this.maxValue) {
			value = Mth.clamp(value, this.maxValue, this.minValue);
		} else {
			value = Mth.clamp(value, this.minValue, this.maxValue);
		}

		return Mth.map(value, this.minValue, this.maxValue, 0D, 1D);
	}

	@Override
	protected void updateMessage() {
		if (this.drawString) {
			this.setMessage(Component.literal("").append(prefix).append(this.getValueString()).append(suffix));
		} else {
			this.setMessage(Component.empty());
		}
	}

	@Override
	protected void applyValue() {
		if (this.onChange != null) {
			this.onChange.onChange(this.getValueInt());
		}
	}

	@Override
	public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
		guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, ((AbstractSliderButtonAccessor) this).armorposer$getSprite(), this.getX(), this.getY(), this.getWidth(), this.getHeight(), ARGB.white(this.alpha));
		guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, ((AbstractSliderButtonAccessor) this).armorposer$getHandleSprite(), this.getX() + (int) (this.value * (double) (this.width - 8)), this.getY(), 8, this.getHeight(), ARGB.white(this.alpha));
		int i = this.active ? 16777215 : 10526880;
		var message = getMessage().copy().withStyle(style -> style.withColor(i));
		this.renderScrollingStringOverContents(guiGraphics.textRendererForWidget(this, GuiGraphics.HoveredTextEffects.NONE), message, 2);

		if (this.isHovered())
			guiGraphics.requestCursor(((AbstractSliderButtonAccessor) this).armorposer$isDragging() ? CursorTypes.RESIZE_EW : CursorTypes.POINTING_HAND);
	}

	public interface OnChange {
		void onChange(int value);
	}
}