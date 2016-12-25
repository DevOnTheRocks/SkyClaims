package net.mohron.skyclaims.island;

import me.ryanhamshire.griefprevention.claim.Claim;
import net.mohron.skyclaims.SkyClaims;
import net.mohron.skyclaims.config.GlobalConfig;
import net.mohron.skyclaims.util.ConfigUtil;
import org.spongepowered.api.world.Location;

import java.util.UUID;

public class IslandTasks {
	private static final SkyClaims PLUGIN = SkyClaims.getInstance();
	private static final int MAX_ISLAND_SIZE = 256; // 16 x 16 chunks
	private static GlobalConfig config = PLUGIN.getConfig();
	private static final int MAX_REGIONS = ConfigUtil.get(config.maxRegions, 256);
	private static int i; // location of the island in a quadrant
	private static int x; // x value of the current region
	private static int z; // y value of the current region

	static {
		// Initialize with stored values before using defaults
		i = 0;
		x = 1;
		z = 1;
	}

	public static Island createIsland(UUID owner) {
		Claim claim = createParentClaim();
		return new Island(owner, claim);
	}

	public static void resetIsland(UUID owner) {
		clearIsland(owner);
		buildIsland(owner, PLUGIN.dataStore.getIsland(owner).getParentClaim());
	}

	private static Claim createParentClaim() {
		Claim claim = new Claim(
				new Location<>(ConfigUtil.getWorld(), x * 512, 0, z * 512),
				new Location<>(ConfigUtil.getWorld(), x * 512 + MAX_ISLAND_SIZE, 256, z * 512 + MAX_ISLAND_SIZE),
				Claim.Type.ADMIN
		);
		if (i == 3) {
			i = 0;
			if (z > MAX_REGIONS) {
				z = 0;
				x++;
			}
		}
		i++;

		return claim;
	}

	public static Claim buildIsland(UUID owner, Claim parentClaim) {
		//TODO Build an "island" in the center of the owners island using a schematic

		return parentClaim; // Return the subclaim
	}

	private static void clearIsland(UUID owner) {
		//TODO Clear island, inventory, enderchest, and supported private mod inventories ie. mod ender chests
	}

}