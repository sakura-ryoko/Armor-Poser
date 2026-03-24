package com.mrbysco.armorposer.config;

import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.ModConfigSpec.BooleanValue;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;


public class PoserConfig {

	public static class Common {
		public final BooleanValue enableConfigGui;
		public final BooleanValue enableNameTags;
		public final BooleanValue allowScrolling;
		public final BooleanValue nameBasedFeatures;

		public final BooleanValue restrictInvisibleToOP;
		public final BooleanValue restrictBasePlateToOP;
		public final BooleanValue restrictGravityToOP;
		public final BooleanValue restrictShowArmsToOP;
		public final BooleanValue restrictSmallToOP;
		public final BooleanValue restrictNameVisibleToOP;
		public final BooleanValue restrictRotationToOP;
		public final BooleanValue restrictAlignToOP;
		public final BooleanValue restrictPositionToOp;
		public final BooleanValue restrictResizeToOP;
		public final ModConfigSpec.ConfigValue<List<? extends String>> restrictWhitelist;

		Common(ModConfigSpec.Builder builder) {
			builder.comment("General settings")
					.translation("armorposer.config.general")
					.push("General");

			enableConfigGui = builder
					.comment("Show the Armor Stand configuration GUI on shift right click")
					.translation("armorposer.config.enableConfigGui")
					.define("enableConfigGui", true);

			enableNameTags = builder
					.comment("Allow Armor Stand to be renamed using name tags")
					.translation("armorposer.config.enableNameTags")
					.define("enableNameTags", true);

			allowScrolling = builder
					.comment("Allow scrolling to increase / decrease an angle value in the posing screen")
					.translation("armorposer.config.allowScrolling")
					.define("allowScrolling", true);

			nameBasedFeatures = builder
					.comment("Allow the spawned Armor Stand to have settings set based on its name")
					.translation("armorposer.config.nameBasedFeatures")
					.define("nameBasedFeatures", false);

			builder.pop();
			builder.comment("Restrict settings")
					.translation("armorposer.config.restrict")
					.push("Restrict");

			restrictInvisibleToOP = builder
					.comment("Restrict the ability to toggle invisibility to server operators")
					.translation("armorposer.config.restrictInvisibleToOP")
					.define("restrictInvisibleToOP", false);

			restrictBasePlateToOP = builder
					.comment("Restrict the ability to toggle the base plate to server operators")
					.translation("armorposer.config.restrictBasePlateToOP")
					.define("restrictBasePlateToOP", false);

			restrictGravityToOP = builder
					.comment("Restrict the ability to toggle gravity to server operators")
					.translation("armorposer.config.restrictGravityToOP")
					.define("restrictGravityToOP", false);

			restrictShowArmsToOP = builder
					.comment("Restrict the ability to toggle arms to server operators")
					.translation("armorposer.config.restrictShowArmsToOP")
					.define("restrictShowArmsToOP", false);

			restrictSmallToOP = builder
					.comment("Restrict the ability to toggle small size to server operators")
					.translation("armorposer.config.restrictSmallToOP")
					.define("restrictSmallToOP", false);

			restrictNameVisibleToOP = builder
					.comment("Restrict the ability to toggle name visibility to server operators")
					.translation("armorposer.config.restrictNameVisibleToOP")
					.define("restrictNameVisibleToOP", false);

			restrictRotationToOP = builder
					.comment("Restrict the ability to rotate the Armor Stand to server operators")
					.translation("armorposer.config.restrictRotationToOP")
					.define("restrictRotationToOP", false);

			restrictAlignToOP = builder
					.comment("Restrict the align options for Armor Stand to server operators")
					.translation("armorposer.config.restrictAlignToOP")
					.define("restrictAlignToOP", false);

			restrictPositionToOp = builder
					.comment("Restrict the ability to position the Armor Stand to server operators")
					.translation("armorposer.config.restrictPositionToOp")
					.define("restrictPositionToOp", false);

			restrictResizeToOP = builder
					.comment("Restrict the ability to resize the Armor Stand to server operators")
					.translation("armorposer.config.restrictResizeToOP")
					.define("restrictResizeToOP", false);

			restrictWhitelist = builder
					.comment("List of players allowed to bypass enabled restrictions. Entries can be either a username to bypass all restrictions (e.g. \"shynieke\") or a specific restriction using the format \"username,feature\" (e.g. \"shynieke,resize\").")
					.translation("armorposer.config.restrictWhitelist")
					.defineListAllowEmpty("restrictWhitelist", List.of(), String::new, o -> o instanceof String);

			builder.pop();
		}

	}

	public static final ModConfigSpec commonSpec;
	public static final Common COMMON;

	static {
		final Pair<Common, ModConfigSpec> specPair = new ModConfigSpec.Builder().configure(Common::new);
		commonSpec = specPair.getRight();
		COMMON = specPair.getLeft();
	}

	public static class Client {
		public final BooleanValue directNametagOnly;
		public final ModConfigSpec.IntValue nametagRenderDistance;

		Client(ModConfigSpec.Builder builder) {
			builder.comment("Client settings")
					.translation("armorposer.config.client")
					.push("Client");

			directNametagOnly = builder
					.comment("Only render the nametag when directly looking at the Armor Stand. Set to false to use vanilla behavior (default: false)")
					.translation("armorposer.config.directNametagOnly")
					.define("directNametagOnly", false);

			nametagRenderDistance = builder
					.comment("The distance squared at which Armor Stand nametags are rendered. Set to 0 to use vanilla behavior (default: 0)")
					.translation("armorposer.config.nametagRenderDistance")
					.defineInRange("nametagRenderDistance", 0, 0, 64);

			builder.pop();
		}

	}

	public static final ModConfigSpec clientSpec;
	public static final Client CLIENT;

	static {
		final Pair<Client, ModConfigSpec> specPair = new ModConfigSpec.Builder().configure(Client::new);
		clientSpec = specPair.getRight();
		CLIENT = specPair.getLeft();
	}
}
