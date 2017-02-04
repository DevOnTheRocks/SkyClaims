package net.mohron.skyclaims.config.type;

import net.mohron.skyclaims.SkyClaims;
import net.mohron.skyclaims.util.WorldUtil;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.world.World;

@ConfigSerializable
public class WorldConfig {
	@Setting(value = "SkyClaims-World", comment = "Name of the world to manage islands in. Default: world")
	private String worldName;
	@Setting(value = "Island-Height", comment = "Height to build islands at. Default: 72")
	private int defaultHeight;
	@Setting(value = "Spawn-Regions", comment = "The height & width of regions to reserve for Spawn. Default: 1")
	private int spawnRegions;

	public WorldConfig() {
		worldName = Sponge.getGame().getServer().getDefaultWorldName();
		defaultHeight = 72;
		spawnRegions = 1;
	}

	public World getWorld() {
		return SkyClaims.getInstance().getGame().getServer().getWorld(worldName).orElse(WorldUtil.getDefaultWorld());
	}

	public String getWorldName() {
		return worldName;
	}

	public int getDefaultHeight() {
		return defaultHeight;
	}

	public int getSpawnRegions() {
		return spawnRegions;
	}
}