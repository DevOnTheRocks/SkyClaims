package net.mohron.skyclaims.lib;

import net.mohron.skyclaims.SkyClaims;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.biome.BiomeType;
import org.spongepowered.api.world.biome.BiomeTypes;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class Arguments {
	private static final SkyClaims PLUGIN = SkyClaims.getInstance();

	// Command Argument Keys
	public static final Text BIOME = Text.of("biome");
	public static final Text CONFIRM = Text.of("confirm");
	public static final Text HELP = Text.of("help");
	public static final Text NAME = Text.of("name");
	public static final Text SCHEMATIC = Text.of("schematic");
	public static final Text TARGET = Text.of("target");
	public static final Text USER = Text.of("user");
	public static final Text UUID = Text.of("uuid");

	// Maps for Choice Command Arguments
	public static final Map<String, String> SCHEMATICS = new HashMap<>();
	public static final Map<String, BiomeType> BIOMES = new HashMap<>();
	public static final Map<String, Target> TARGETS = new HashMap<>();

	static {
		loadSchematics();

		// Standard Biomes
		BIOMES.put(getCommandArg(BiomeTypes.BEACH), BiomeTypes.BEACH);
		BIOMES.put(getCommandArg(BiomeTypes.BIRCH_FOREST), BiomeTypes.BIRCH_FOREST);
		BIOMES.put(getCommandArg(BiomeTypes.BIRCH_FOREST_HILLS), BiomeTypes.BIRCH_FOREST_HILLS);
		BIOMES.put(getCommandArg(BiomeTypes.COLD_BEACH), BiomeTypes.COLD_BEACH);
		BIOMES.put(getCommandArg(BiomeTypes.COLD_TAIGA), BiomeTypes.COLD_TAIGA);
		BIOMES.put(getCommandArg(BiomeTypes.COLD_TAIGA_HILLS), BiomeTypes.COLD_TAIGA_HILLS);
		BIOMES.put(getCommandArg(BiomeTypes.DEEP_OCEAN), BiomeTypes.DEEP_OCEAN);
		BIOMES.put(getCommandArg(BiomeTypes.DESERT), BiomeTypes.DESERT);
		BIOMES.put(getCommandArg(BiomeTypes.DESERT_HILLS), BiomeTypes.DESERT_HILLS);
		BIOMES.put(getCommandArg(BiomeTypes.EXTREME_HILLS), BiomeTypes.EXTREME_HILLS);
		BIOMES.put(getCommandArg(BiomeTypes.EXTREME_HILLS_EDGE), BiomeTypes.EXTREME_HILLS_EDGE);
		BIOMES.put(getCommandArg(BiomeTypes.EXTREME_HILLS_PLUS), BiomeTypes.EXTREME_HILLS_PLUS);
		BIOMES.put(getCommandArg(BiomeTypes.FOREST), BiomeTypes.FOREST);
		BIOMES.put(getCommandArg(BiomeTypes.FOREST_HILLS), BiomeTypes.FOREST_HILLS);
		BIOMES.put(getCommandArg(BiomeTypes.FROZEN_OCEAN), BiomeTypes.FROZEN_OCEAN);
		BIOMES.put(getCommandArg(BiomeTypes.FROZEN_RIVER), BiomeTypes.FROZEN_RIVER);
		BIOMES.put(getCommandArg(BiomeTypes.HELL), BiomeTypes.HELL);
		BIOMES.put(getCommandArg(BiomeTypes.ICE_MOUNTAINS), BiomeTypes.ICE_MOUNTAINS);
		BIOMES.put(getCommandArg(BiomeTypes.ICE_PLAINS), BiomeTypes.ICE_PLAINS);
		BIOMES.put(getCommandArg(BiomeTypes.JUNGLE), BiomeTypes.JUNGLE);
		BIOMES.put(getCommandArg(BiomeTypes.JUNGLE_EDGE), BiomeTypes.JUNGLE_EDGE);
		BIOMES.put(getCommandArg(BiomeTypes.JUNGLE_HILLS), BiomeTypes.JUNGLE_HILLS);
		BIOMES.put(getCommandArg(BiomeTypes.MEGA_TAIGA), BiomeTypes.MEGA_TAIGA);
		BIOMES.put(getCommandArg(BiomeTypes.MEGA_TAIGA_HILLS), BiomeTypes.MEGA_TAIGA_HILLS);
		BIOMES.put(getCommandArg(BiomeTypes.MESA), BiomeTypes.MESA);
		BIOMES.put(getCommandArg(BiomeTypes.MESA_PLATEAU), BiomeTypes.MESA_PLATEAU);
		BIOMES.put(getCommandArg(BiomeTypes.MESA_PLATEAU_FOREST), BiomeTypes.MESA_PLATEAU_FOREST);
		BIOMES.put(getCommandArg(BiomeTypes.MUSHROOM_ISLAND), BiomeTypes.MUSHROOM_ISLAND);
		BIOMES.put(getCommandArg(BiomeTypes.MUSHROOM_ISLAND_SHORE), BiomeTypes.MUSHROOM_ISLAND_SHORE);
		BIOMES.put(getCommandArg(BiomeTypes.OCEAN), BiomeTypes.OCEAN);
		BIOMES.put(getCommandArg(BiomeTypes.PLAINS), BiomeTypes.PLAINS);
		BIOMES.put(getCommandArg(BiomeTypes.RIVER), BiomeTypes.RIVER);
		BIOMES.put(getCommandArg(BiomeTypes.ROOFED_FOREST), BiomeTypes.ROOFED_FOREST);
		BIOMES.put(getCommandArg(BiomeTypes.SAVANNA), BiomeTypes.SAVANNA);
		BIOMES.put(getCommandArg(BiomeTypes.SAVANNA_PLATEAU), BiomeTypes.SAVANNA_PLATEAU);
		BIOMES.put(getCommandArg(BiomeTypes.SKY), BiomeTypes.SKY);
		BIOMES.put(getCommandArg(BiomeTypes.STONE_BEACH), BiomeTypes.STONE_BEACH);
		BIOMES.put(getCommandArg(BiomeTypes.SWAMPLAND), BiomeTypes.SWAMPLAND);
		BIOMES.put(getCommandArg(BiomeTypes.TAIGA), BiomeTypes.TAIGA);
		BIOMES.put(getCommandArg(BiomeTypes.TAIGA_HILLS), BiomeTypes.TAIGA_HILLS);
		// Mutated Biomes
		BIOMES.put(getCommandArg(BiomeTypes.BIRCH_FOREST_HILLS_MOUNTAINS), BiomeTypes.BIRCH_FOREST_HILLS_MOUNTAINS);
		BIOMES.put(getCommandArg(BiomeTypes.BIRCH_FOREST_MOUNTAINS), BiomeTypes.BIRCH_FOREST_MOUNTAINS);
		BIOMES.put(getCommandArg(BiomeTypes.COLD_TAIGA_MOUNTAINS), BiomeTypes.COLD_TAIGA_MOUNTAINS);
		BIOMES.put(getCommandArg(BiomeTypes.DESERT_MOUNTAINS), BiomeTypes.DESERT_MOUNTAINS);
		BIOMES.put(getCommandArg(BiomeTypes.EXTREME_HILLS_MOUNTAINS), BiomeTypes.EXTREME_HILLS_MOUNTAINS);
		BIOMES.put(getCommandArg(BiomeTypes.EXTREME_HILLS_PLUS_MOUNTAINS), BiomeTypes.EXTREME_HILLS_PLUS_MOUNTAINS);
		BIOMES.put(getCommandArg(BiomeTypes.FLOWER_FOREST), BiomeTypes.FLOWER_FOREST);
		BIOMES.put(getCommandArg(BiomeTypes.ICE_PLAINS_SPIKES), BiomeTypes.ICE_PLAINS_SPIKES);
		BIOMES.put(getCommandArg(BiomeTypes.JUNGLE_EDGE_MOUNTAINS), BiomeTypes.JUNGLE_EDGE_MOUNTAINS);
		BIOMES.put(getCommandArg(BiomeTypes.JUNGLE_MOUNTAINS), BiomeTypes.JUNGLE_MOUNTAINS);
		BIOMES.put(getCommandArg(BiomeTypes.MEGA_SPRUCE_TAIGA), BiomeTypes.MEGA_SPRUCE_TAIGA);
		BIOMES.put(getCommandArg(BiomeTypes.MEGA_SPRUCE_TAIGA_HILLS), BiomeTypes.MEGA_SPRUCE_TAIGA_HILLS);
		BIOMES.put(getCommandArg(BiomeTypes.MESA_BRYCE), BiomeTypes.MESA_BRYCE);
		BIOMES.put(getCommandArg(BiomeTypes.MESA_PLATEAU_FOREST_MOUNTAINS), BiomeTypes.MESA_PLATEAU_FOREST_MOUNTAINS);
		BIOMES.put(getCommandArg(BiomeTypes.MESA_PLATEAU_MOUNTAINS), BiomeTypes.MESA_PLATEAU_MOUNTAINS);
		BIOMES.put(getCommandArg(BiomeTypes.ROOFED_FOREST_MOUNTAINS), BiomeTypes.ROOFED_FOREST_MOUNTAINS);
		BIOMES.put(getCommandArg(BiomeTypes.SAVANNA_MOUNTAINS), BiomeTypes.SAVANNA_MOUNTAINS);
		BIOMES.put(getCommandArg(BiomeTypes.SAVANNA_PLATEAU_MOUNTAINS), BiomeTypes.SAVANNA_PLATEAU_MOUNTAINS);
		BIOMES.put(getCommandArg(BiomeTypes.SUNFLOWER_PLAINS), BiomeTypes.SUNFLOWER_PLAINS);
		BIOMES.put(getCommandArg(BiomeTypes.SWAMPLAND_MOUNTAINS), BiomeTypes.SWAMPLAND_MOUNTAINS);
		BIOMES.put(getCommandArg(BiomeTypes.TAIGA_MOUNTAINS), BiomeTypes.TAIGA_MOUNTAINS);
		BIOMES.put(getCommandArg(BiomeTypes.VOID), BiomeTypes.VOID);

		TARGETS.put("block", Target.BLOCK);
		TARGETS.put("chunk", Target.CHUNK);
		TARGETS.put("island", Target.ISLAND);
	}

	@SuppressWarnings("ConstantConditions")
	public static void loadSchematics() {
		SCHEMATICS.clear();
		File schemDir = new File(PLUGIN.getConfigDir() + File.separator + "schematics");
		try {
			PLUGIN.getLogger().info("Attempting to retrieve all schematics!");
			for (File file : schemDir.listFiles()) {
				PLUGIN.getLogger().info("Found File: " + file);
				String schem = file.getName();
				if (schem.endsWith(".schematic")) {
					SCHEMATICS.put(schem.replace(".schematic", "").toLowerCase(), schem.replace(".schematic", ""));
					PLUGIN.getLogger().info("Added Schematic: " + schem);
				}
			}
		} catch (NullPointerException e) {
			PLUGIN.getLogger().error("Failed to read schematics directory!");
		}
	}

	public enum Target {
		BLOCK, CHUNK, ISLAND;
	}

	private static String getCommandArg(BiomeType biomeType) {
		return biomeType.getName().replaceAll(" ", "_").toLowerCase();
	}
}