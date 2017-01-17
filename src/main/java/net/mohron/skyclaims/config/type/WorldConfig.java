package net.mohron.skyclaims.config.type;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;
import org.spongepowered.api.Sponge;

@ConfigSerializable
public class WorldConfig {
	@Setting(value = "SkyClaims-World", comment = "Name of the world to manage islands in. Default: world")
	public String worldName;
	@Setting(value = "Island-Height", comment = "Height to build islands at. Default: 72")
	public Integer defaultHeight;
	@Setting(value = "Spawn-Regions", comment = "The height & width of regions to reserve for Spawn. Default: 1")
	public Integer spawnRegions;

	public WorldConfig() {
		worldName = Sponge.getGame().getServer().getDefaultWorldName();
		defaultHeight = 72;
		spawnRegions = 1;
	}
}