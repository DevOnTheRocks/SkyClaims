package net.mohron.skyclaims.lib;

import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.biome.BiomeType;
import org.spongepowered.api.world.biome.BiomeTypes;

import java.util.HashMap;
import java.util.Map;

public class Arguments {
	// Command Argument Keys
	public static final Text SUBCOMMAND = Text.of("admin");
	public static final Text NAME = Text.of("name");
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
		SCHEMATICS.put("island", "island");

		// Standard Biomes
		BIOMES.put(BiomeTypes.BEACH.getName(), BiomeTypes.BEACH);
		BIOMES.put(BiomeTypes.BIRCH_FOREST.getName(), BiomeTypes.BIRCH_FOREST);
		BIOMES.put(BiomeTypes.BIRCH_FOREST_HILLS.getName(), BiomeTypes.BIRCH_FOREST_HILLS);
		BIOMES.put(BiomeTypes.COLD_BEACH.getName(), BiomeTypes.COLD_BEACH);
		BIOMES.put(BiomeTypes.COLD_TAIGA.getName(), BiomeTypes.COLD_TAIGA);
		BIOMES.put(BiomeTypes.COLD_TAIGA_HILLS.getName(), BiomeTypes.COLD_TAIGA_HILLS);
		BIOMES.put(BiomeTypes.DEEP_OCEAN.getName(), BiomeTypes.DEEP_OCEAN);
		BIOMES.put(BiomeTypes.DESERT.getName(), BiomeTypes.DESERT);
		BIOMES.put(BiomeTypes.DESERT_HILLS.getName(), BiomeTypes.DESERT_HILLS);
		BIOMES.put(BiomeTypes.EXTREME_HILLS.getName(), BiomeTypes.EXTREME_HILLS);
		BIOMES.put(BiomeTypes.EXTREME_HILLS_EDGE.getName(), BiomeTypes.EXTREME_HILLS_EDGE);
		BIOMES.put(BiomeTypes.EXTREME_HILLS_PLUS.getName(), BiomeTypes.EXTREME_HILLS_PLUS);
		BIOMES.put(BiomeTypes.FOREST.getName(), BiomeTypes.FOREST);
		BIOMES.put(BiomeTypes.FOREST_HILLS.getName(), BiomeTypes.FOREST_HILLS);
		BIOMES.put(BiomeTypes.FROZEN_OCEAN.getName(), BiomeTypes.FROZEN_OCEAN);
		BIOMES.put(BiomeTypes.FROZEN_RIVER.getName(), BiomeTypes.FROZEN_RIVER);
		BIOMES.put(BiomeTypes.HELL.getName(), BiomeTypes.HELL);
		BIOMES.put(BiomeTypes.ICE_MOUNTAINS.getName(), BiomeTypes.ICE_MOUNTAINS);
		BIOMES.put(BiomeTypes.ICE_PLAINS.getName(), BiomeTypes.ICE_PLAINS);
		BIOMES.put(BiomeTypes.JUNGLE.getName(), BiomeTypes.JUNGLE);
		BIOMES.put(BiomeTypes.JUNGLE_EDGE.getName(), BiomeTypes.JUNGLE_EDGE);
		BIOMES.put(BiomeTypes.JUNGLE_HILLS.getName(), BiomeTypes.JUNGLE_HILLS);
		BIOMES.put(BiomeTypes.MEGA_TAIGA.getName(), BiomeTypes.MEGA_TAIGA);
		BIOMES.put(BiomeTypes.MEGA_TAIGA_HILLS.getName(), BiomeTypes.MEGA_TAIGA_HILLS);
		BIOMES.put(BiomeTypes.MESA.getName(), BiomeTypes.MESA);
		BIOMES.put(BiomeTypes.MESA_PLATEAU.getName(), BiomeTypes.MESA_PLATEAU);
		BIOMES.put(BiomeTypes.MESA_PLATEAU_FOREST.getName(), BiomeTypes.MESA_PLATEAU_FOREST);
		BIOMES.put(BiomeTypes.MUSHROOM_ISLAND.getName(), BiomeTypes.MUSHROOM_ISLAND);
		BIOMES.put(BiomeTypes.MUSHROOM_ISLAND_SHORE.getName(), BiomeTypes.MUSHROOM_ISLAND_SHORE);
		BIOMES.put(BiomeTypes.OCEAN.getName(), BiomeTypes.OCEAN);
		BIOMES.put(BiomeTypes.PLAINS.getName(), BiomeTypes.PLAINS);
		BIOMES.put(BiomeTypes.RIVER.getName(), BiomeTypes.RIVER);
		BIOMES.put(BiomeTypes.ROOFED_FOREST.getName(), BiomeTypes.ROOFED_FOREST);
		BIOMES.put(BiomeTypes.SAVANNA.getName(), BiomeTypes.SAVANNA);
		BIOMES.put(BiomeTypes.SAVANNA_PLATEAU.getName(), BiomeTypes.SAVANNA_PLATEAU);
		BIOMES.put(BiomeTypes.SKY.getName(), BiomeTypes.SKY);
		BIOMES.put(BiomeTypes.STONE_BEACH.getName(), BiomeTypes.STONE_BEACH);
		BIOMES.put(BiomeTypes.SWAMPLAND.getName(), BiomeTypes.SWAMPLAND);
		BIOMES.put(BiomeTypes.TAIGA.getName(), BiomeTypes.TAIGA);
		BIOMES.put(BiomeTypes.TAIGA_HILLS.getName(), BiomeTypes.TAIGA_HILLS);
		// Mutated Biomes
		BIOMES.put(BiomeTypes.BIRCH_FOREST_HILLS_MOUNTAINS.getName(), BiomeTypes.BIRCH_FOREST_HILLS_MOUNTAINS);
		BIOMES.put(BiomeTypes.BIRCH_FOREST_MOUNTAINS.getName(), BiomeTypes.BIRCH_FOREST_MOUNTAINS);
		BIOMES.put(BiomeTypes.COLD_TAIGA_MOUNTAINS.getName(), BiomeTypes.COLD_TAIGA_MOUNTAINS);
		BIOMES.put(BiomeTypes.DESERT_MOUNTAINS.getName(), BiomeTypes.DESERT_MOUNTAINS);
		BIOMES.put(BiomeTypes.EXTREME_HILLS_MOUNTAINS.getName(), BiomeTypes.EXTREME_HILLS_MOUNTAINS);
		BIOMES.put(BiomeTypes.EXTREME_HILLS_PLUS_MOUNTAINS.getName(), BiomeTypes.EXTREME_HILLS_PLUS_MOUNTAINS);
		BIOMES.put(BiomeTypes.FLOWER_FOREST.getName(), BiomeTypes.FLOWER_FOREST);
		BIOMES.put(BiomeTypes.ICE_PLAINS_SPIKES.getName(), BiomeTypes.ICE_PLAINS_SPIKES);
		BIOMES.put(BiomeTypes.JUNGLE_EDGE_MOUNTAINS.getName(), BiomeTypes.JUNGLE_EDGE_MOUNTAINS);
		BIOMES.put(BiomeTypes.JUNGLE_MOUNTAINS.getName(), BiomeTypes.JUNGLE_MOUNTAINS);
		BIOMES.put(BiomeTypes.MEGA_SPRUCE_TAIGA.getName(), BiomeTypes.MEGA_SPRUCE_TAIGA);
		BIOMES.put(BiomeTypes.MEGA_SPRUCE_TAIGA_HILLS.getName(), BiomeTypes.MEGA_SPRUCE_TAIGA_HILLS);
		BIOMES.put(BiomeTypes.MESA_BRYCE.getName(), BiomeTypes.MESA_BRYCE);
		BIOMES.put(BiomeTypes.MESA_PLATEAU_FOREST_MOUNTAINS.getName(), BiomeTypes.MESA_PLATEAU_FOREST_MOUNTAINS);
		BIOMES.put(BiomeTypes.MESA_PLATEAU_MOUNTAINS.getName(), BiomeTypes.MESA_PLATEAU_MOUNTAINS);
		BIOMES.put(BiomeTypes.ROOFED_FOREST_MOUNTAINS.getName(), BiomeTypes.ROOFED_FOREST_MOUNTAINS);
		BIOMES.put(BiomeTypes.SAVANNA_MOUNTAINS.getName(), BiomeTypes.SAVANNA_MOUNTAINS);
		BIOMES.put(BiomeTypes.SAVANNA_PLATEAU_MOUNTAINS.getName(), BiomeTypes.SAVANNA_PLATEAU_MOUNTAINS);
		BIOMES.put(BiomeTypes.SUNFLOWER_PLAINS.getName(), BiomeTypes.SUNFLOWER_PLAINS);
		BIOMES.put(BiomeTypes.SWAMPLAND_MOUNTAINS.getName(), BiomeTypes.SWAMPLAND_MOUNTAINS);
		BIOMES.put(BiomeTypes.TAIGA_MOUNTAINS.getName(), BiomeTypes.TAIGA_MOUNTAINS);
		BIOMES.put(BiomeTypes.VOID.getName(), BiomeTypes.VOID);

		TARGETS.put("block", Target.BLOCK);
		TARGETS.put("chunk", Target.CHUNK);
		TARGETS.put("island", Target.ISLAND);
	}

	public enum Target {
		BLOCK, CHUNK, ISLAND;
	}
}