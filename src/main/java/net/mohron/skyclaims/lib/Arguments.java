package net.mohron.skyclaims.lib;

import net.mohron.skyclaims.SkyClaims;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.biome.BiomeType;
import org.spongepowered.api.world.biome.BiomeTypes;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
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
	public static final Map<String, File> SCHEMATICS = new HashMap<>();
	public static final Map<String, BiomeType> BIOMES = new HashMap<>();
	public static final Map<String, Target> TARGETS = new HashMap<>();

	static {
		for (String schem : SkyClaims.getInstance().getConfig().schematics) {
			String path = String.format("%s\\%s", SkyClaims.getInstance().getConfigDir(), schem);
			if (Files.exists(Paths.get(path)))
				Arguments.SCHEMATICS.put(schem, new File(path));
		}

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