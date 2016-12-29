package net.mohron.skyclaims.util;

import net.mohron.skyclaims.SkyClaims;
import org.spongepowered.api.world.World;

public class WorldUtil {
	public static World getDefaultWorld() {
		String defaultWorldName = SkyClaims.getInstance().getGame().getServer().getDefaultWorldName();
		return SkyClaims.getInstance().getGame().getServer().getWorld(defaultWorldName).get();
	}
}
