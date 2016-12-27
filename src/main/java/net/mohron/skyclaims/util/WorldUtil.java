package net.mohron.skyclaims.util;

import net.mohron.skyclaims.SkyClaims;
import org.spongepowered.api.world.World;

/**
 * Created by cossacksman on 27/12/16.
 */
public class WorldUtil {
    public static World getDefaultWorld() {
        String defaultWorldName = SkyClaims.getInstance().getGame().getServer().getDefaultWorldName();
        return SkyClaims.getInstance().getGame().getServer().getWorld(defaultWorldName).get();
    }
}
