package net.mohron.skyclaims.util;

import net.mohron.skyclaims.SkyClaims;
import net.mohron.skyclaims.config.GlobalConfig;
import org.spongepowered.api.Server;
import org.spongepowered.api.world.World;

import java.util.Optional;

public class ConfigUtil {
	private static final SkyClaims PLUGIN = SkyClaims.getInstance();
	private static GlobalConfig config = PLUGIN.getConfig();

	public static int get(Integer config, int defaultValue) {
		return (config != null) ? config : defaultValue;
	}

	public static World getWorld() {
		Server server = PLUGIN.getGame().getServer();
		Optional<World> world = server.getWorld(config.world);
		Optional<World> defaultWorld = server.getWorld(server.getDefaultWorldName());
		return (world.isPresent()) ? world.get() : defaultWorld.get();
	}
}