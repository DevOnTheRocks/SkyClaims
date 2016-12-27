package net.mohron.skyclaims.config;

import ninja.leaping.configurate.objectmapping.Setting;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.world.biome.BiomeType;
import org.spongepowered.api.world.biome.BiomeTypes;

public class GlobalConfig {
	@Setting(value = "Database")
	public DatabaseConfig database;
	@Setting(value = "SkyClaims-Dimension")
	public String world;
	@Setting(value = "Default-Biome")
	public BiomeType defaultBiome;
	@Setting(value = "Max-Regions")
	public Integer maxRegions;
	@Setting(value = "Island-Height")
	public Integer defaultHeight;

	public GlobalConfig() {
		database = new DatabaseConfig();
		world = Sponge.getGame().getServer().getDefaultWorldName();
		defaultBiome = BiomeTypes.PLAINS;
		maxRegions = 256;
		defaultHeight = 64;
	}
}