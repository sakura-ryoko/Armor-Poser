package com.mrbysco.armorposer.config;

import com.mrbysco.armorposer.Reference;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import net.minecraft.client.gui.screens.Screen;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;

public class PoserModMenuIntegration implements ModMenuApi {
	@Override
	public ConfigScreenFactory<?> getModConfigScreenFactory() {
		return (Screen screen) -> new ConfigurationScreen(Reference.MOD_ID, screen);
	}
}
