package net.mohron.skyclaims.lib;

import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.biome.BiomeType;
import org.spongepowered.api.world.biome.BiomeTypes;

import java.util.HashMap;
import java.util.Map;

public class Arguments {
	// Command Argument Keys
	public static final Text SCHEMATIC = Text.of("schematic");
	public static final Text USER = Text.of("user");
	public static final Text CONFIRM = Text.of("confirm");
	public static final Text BIOME = Text.of("biome");
	public static final Text TARGET = Text.of("target");

	// Maps for Choice Command Arguments
	public static final Map<String, String> SCHEMATICS = new HashMap<>();
	public static final Map<String, BiomeType> BIOMES = new HashMap<>();
	public static final Map<String, Target> TARGETS = new HashMap<>();

	static {
		// TODO Add all *.schematics file names to SCHEMATICS

		BIOMES.put("forest", BiomeTypes.FOREST);
		BIOMES.put("plains", BiomeTypes.PLAINS);

		TARGETS.put("block", Target.BLOCK);
		TARGETS.put("chunk", Target.CHUNK);
		TARGETS.put("island", Target.ISLAND);
	}

	public enum Target {
		BLOCK, CHUNK, ISLAND;
	}
}