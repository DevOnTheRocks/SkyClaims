package net.mohron.skyclaims.config;

import ninja.leaping.configurate.objectmapping.Setting;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.world.biome.BiomeType;
import org.spongepowered.api.world.biome.BiomeTypes;

public class GlobalConfig {
	public GlobalConfig() {
		database = new DatabaseConfig();
		skyClaimsDimension = Sponge.getGame().getServer().getDefaultWorldName();
		defaultBiome = BiomeTypes.PLAINS;
	}

	@Setting
	public DatabaseConfig database;
	@Setting
	public String skyClaimsDimension;
	@Setting
	public BiomeType defaultBiome;
}