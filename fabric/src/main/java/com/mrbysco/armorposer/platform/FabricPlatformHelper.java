package com.mrbysco.armorposer.platform;

import com.mrbysco.armorposer.Reference;
import com.mrbysco.armorposer.config.PoserConfig;
import com.mrbysco.armorposer.data.RenameData;
import com.mrbysco.armorposer.data.SwapData;
import com.mrbysco.armorposer.data.SyncData;
import com.mrbysco.armorposer.packets.ArmorStandRenamePayload;
import com.mrbysco.armorposer.packets.ArmorStandSwapPayload;
import com.mrbysco.armorposer.packets.ArmorStandSyncPayload;
import com.mrbysco.armorposer.platform.services.IPlatformHelper;
import me.shedaniel.autoconfig.AutoConfig;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.TagValueOutput;

import java.nio.file.Path;
import java.util.List;

public class FabricPlatformHelper implements IPlatformHelper {
	@Override
	public void updateEntity(ArmorStand armorStand, CompoundTag compound) {
		// Create a new TagValueOutput to save the armor stand's data
		try (ProblemReporter.ScopedCollector problemreporter$scopedcollector = new ProblemReporter.ScopedCollector(Reference.LOGGER)) {
			TagValueOutput output = TagValueOutput.createWithContext(problemreporter$scopedcollector, armorStand.registryAccess());

			// Save the armor stand's current state without an ID
			armorStand.saveWithoutId(output);
			// Build the result compound tag from the output
			CompoundTag outputCompound = output.buildResult();
			// Merge the provided compound data into the output compound
			outputCompound.merge(compound);

			// Load the armor stand with the updated compound data
			armorStand.load(TagValueInput.create(ProblemReporter.DISCARDING, armorStand.registryAccess(), outputCompound));

			SyncData data = new SyncData(armorStand.getUUID(), outputCompound);
			ClientPlayNetworking.send(new ArmorStandSyncPayload(data));
		}
	}

	@Override
	public void swapSlots(ArmorStand armorStand, SwapData.Action action) {
		SwapData data = new SwapData(armorStand.getUUID(), action);
		ClientPlayNetworking.send(new ArmorStandSwapPayload(data));
	}

	@Override
	public void renameArmorStand(ArmorStand armorStand, String newName) {
		RenameData data = new RenameData(armorStand.getUUID(), newName);
		ClientPlayNetworking.send(new ArmorStandRenamePayload(data));
	}

	@Override
	public boolean allowScrolling() {
		PoserConfig config = AutoConfig.getConfigHolder(PoserConfig.class).getConfig();
		return config.general.allowScrolling;
	}

	@Override
	public Path getUserPresetFolder() {
		return FabricLoader.getInstance().getConfigDir();
	}

	@Override
	public boolean isResizeRestrictedToOPS() {
		PoserConfig config = AutoConfig.getConfigHolder(PoserConfig.class).getConfig();
		return config.general.restrictResizeToOP;
	}

	@Override
	public List<? extends String> getResizeWhitelist() {
		PoserConfig config = AutoConfig.getConfigHolder(PoserConfig.class).getConfig();
		return config.general.resizeWhitelist;
	}

	@Override
	public boolean nameBasedFeatures() {
		PoserConfig config = AutoConfig.getConfigHolder(PoserConfig.class).getConfig();
		return config.general.nameBasedFeatures;
	}

	@Override
	public String getModVersion() {
		return FabricLoader.getInstance().getModContainer(Reference.MOD_ID).orElseThrow().getMetadata().getVersion().getFriendlyString();
	}
}
