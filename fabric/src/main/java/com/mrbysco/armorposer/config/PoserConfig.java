package com.mrbysco.armorposer.config;

import com.mrbysco.armorposer.Reference;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.autoconfig.annotation.ConfigEntry.Gui.CollapsibleObject;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;

import java.util.List;

@Config(name = Reference.MOD_ID)
public class PoserConfig implements ConfigData {
	@CollapsibleObject
	public General general = new General();

	@CollapsibleObject
	public Restrict restrict = new Restrict();

	public static class General {
		@ConfigEntry.Gui.Tooltip
		@Comment("Show the Armor Stand configuration GUI on shift right click")
		public boolean enableConfigGui = true;
		@ConfigEntry.Gui.Tooltip
		@Comment("Allow Armor Stand to be renamed using name tags")
		public boolean enableNameTags = true;
		@ConfigEntry.Gui.Tooltip
		@Comment("Allow scrolling to add / decrease an angle value in the posing screen")
		public boolean allowScrolling = true;
		@ConfigEntry.Gui.Tooltip
		@Comment("Allow the spawned Armor Stand to have settings set based on its name")
		public boolean nameBasedFeatures = false;
	}

	public static class Restrict {
		@ConfigEntry.Gui.Tooltip
		@Comment("Restrict the ability to toggle invisibility to server operators")
		public boolean restrictInvisibleToOP = false;
		@ConfigEntry.Gui.Tooltip
		@Comment("Restrict the ability to toggle the base plate to server operators")
		public boolean restrictBasePlateToOP = false;
		@ConfigEntry.Gui.Tooltip
		@Comment("Restrict the ability to toggle gravity to server operators")
		public boolean restrictGravityToOP = false;
		@ConfigEntry.Gui.Tooltip
		@Comment("Restrict the ability to toggle arms to server operators")
		public boolean restrictShowArmsToOP = false;
		@ConfigEntry.Gui.Tooltip
		@Comment("Restrict the ability to toggle small size to server operators")
		public boolean restrictSmallToOP = false;
		@ConfigEntry.Gui.Tooltip
		@Comment("Restrict the ability to toggle name visibility to server operators")
		public boolean restrictNameVisibleToOP = false;
		@ConfigEntry.Gui.Tooltip
		@Comment("Restrict the ability to rotate the Armor Stand to server operators")
		public boolean restrictRotationToOP = false;
		@ConfigEntry.Gui.Tooltip
		@Comment("Restrict the align options for Armor Stand to server operators")
		public boolean restrictAlignToOP = false;
		@Comment("Restrict the ability to resize the Armor Stand to server operators")
		public boolean restrictResizeToOP = false;
		@Deprecated
		@ConfigEntry.Gui.Tooltip
		@Comment("List of players that are allowed to resize the Armor Stand when restrictResizeToOP is enabled")
		public List<String> resizeWhitelist = List.of();
		@ConfigEntry.Gui.Tooltip
		@Comment("List of players allowed to bypass enabled restrictions. Entries can be either a username to bypass all restrictions (e.g. \"shynieke\") or a specific restriction using the format \"username,feature\" (e.g. \"shynieke,resize\").")
		public List<String> restrictWhitelist = List.of();
	}

	@CollapsibleObject
	public Client client = new Client();

	public static class Client {
		@ConfigEntry.Gui.Tooltip
		@Comment("Only render the nametag when directly looking at the Armor Stand. Set to false to use vanilla behavior (default: false)")
		public boolean directNametagOnly = false;
		@ConfigEntry.Gui.Tooltip
		@Comment("The distance squared at which Armor Stand nametags are rendered. Set to 0 to use vanilla behavior (default: 0)")
		public int nametagRenderDistance = 0;
	}
}
