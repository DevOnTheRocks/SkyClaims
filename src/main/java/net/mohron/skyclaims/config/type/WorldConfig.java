package net.mohron.skyclaims.config.type;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.world.biome.BiomeTypes;

@ConfigSerializable
public class WorldConfig {
	@Setting(value = "SkyClaims-World", comment = "Name of the world to manage islands in. Default: world")
	public String worldName;
	@Setting(value = "Default-Biome", comment = "Name of the biome to change islands to upon creation. Default: null")
	public String defaultBiome;
	@Setting(value = "Island-Height", comment = "Height to build islands at. Default: 64")
	public Integer defaultHeight;

	public WorldConfig() {
		worldName = Sponge.getGame().getServer().getDefaultWorldName();
		defaultBiome = null;
		defaultHeight = 64;
	}
}
