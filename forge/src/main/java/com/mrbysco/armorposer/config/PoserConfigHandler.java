package com.mrbysco.armorposer.config;


import com.mrbysco.armorposer.Reference;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.event.config.ModConfigEvent;

public class PoserConfigHandler {

	@SubscribeEvent
	public static void onLoad(final ModConfigEvent.Loading configEvent) {
		Reference.LOGGER.debug("Loaded {}'s config file {}", Reference.MOD_ID, configEvent.getConfig().getFileName());
	}

	@SubscribeEvent
	public static void onFileChange(final ModConfigEvent.Reloading configEvent) {
		Reference.LOGGER.debug("{}'s config just got changed on the file system!", Reference.MOD_ID);
	}
}
