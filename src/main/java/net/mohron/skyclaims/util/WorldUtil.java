package net.mohron.skyclaims.util;

import me.ryanhamshire.griefprevention.api.claim.ClaimManager;
import net.mohron.skyclaims.SkyClaims;
import net.mohron.skyclaims.world.Island;
import net.mohron.skyclaims.world.region.Region;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.biome.BiomeType;


public class WorldUtil {
	private static final SkyClaims PLUGIN = SkyClaims.getInstance();
	private static final ClaimManager CLAIM_MANAGER = PLUGIN.getGriefPrevention().getClaimManager(ConfigUtil.getWorld());


	public static World getDefaultWorld() {
		String defaultWorldName = SkyClaims.getInstance().getGame().getServer().getDefaultWorldName();
		return SkyClaims.getInstance().getGame().getServer().getWorld(defaultWorldName).get();
	}

	public static void setBlockBiome(Location<World> location, BiomeType biomeType) {
		location.getExtent().setBiome(
				location.getBlockX(),
				0,
				location.getBlockZ(),
				biomeType);
	}

	public static void setChunkBiome(Location<World> location, BiomeType biomeType) {
		for (int x = 0; x < 16; x++) {
			for (int z = 0; z < 16; z++) {
				location.getExtent().setBiome(
						location.getChunkPosition().getX() * 16 + x,
						0,
						location.getChunkPosition().getZ() * 16 + z,
						biomeType);
			}
		}
	}

	public static void setIslandBiome(Island island, BiomeType biomeType) {
		if (island.getClaim().isPresent()) {
			int x1 = island.getClaim().get().getLesserBoundaryCorner().getBlockX();
			int x2 = island.getClaim().get().getGreaterBoundaryCorner().getBlockX();
			int z1 = island.getClaim().get().getLesserBoundaryCorner().getBlockZ();
			int z2 = island.getClaim().get().getGreaterBoundaryCorner().getBlockZ();
			for (int x = x1; x < x2; x++) {
				for (int z = z1; z < z2; z++) {
					island.getWorld().setBiome(
							x,
							0,
							z,
							biomeType);
				}
			}
		} else setRegionBiome(island.getRegion(), biomeType);
	}

	public static void setRegionBiome(Region region, BiomeType biomeType) {
		World world = ConfigUtil.getWorld();
		for (int x = region.getLesserBoundary().getX(); x < region.getGreaterBoundary().getX(); x++) {
			for (int z = region.getLesserBoundary().getZ(); z < region.getGreaterBoundary().getZ(); z++) {
				world.setBiome(x, 0, z, biomeType);
			}
		}
	}
}