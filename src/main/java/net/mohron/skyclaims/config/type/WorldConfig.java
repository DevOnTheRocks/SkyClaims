package net.mohron.skyclaims.config.type;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.world.biome.BiomeTypes;

@ConfigSerializable
public class WorldConfig {
	@Setting(value = "SkyClaims-Dimension")
	public String worldName;
	@Setting(value = "Default-Biome")
	public String defaultBiome;
	@Setting(value = "Island-Height")
	public Integer defaultHeight;

	public WorldConfig() {
		worldName = Sponge.getGame().getServer().getDefaultWorldName();
		defaultBiome = BiomeTypes.PLAINS.getName();
		defaultHeight = 64;
	}
}
