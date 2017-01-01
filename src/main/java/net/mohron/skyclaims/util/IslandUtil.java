package net.mohron.skyclaims.util;

import com.flowpowered.math.vector.Vector3i;
import me.ryanhamshire.griefprevention.DataStore;
import me.ryanhamshire.griefprevention.claim.Claim;
import net.mohron.skyclaims.SkyClaims;
import net.mohron.skyclaims.claim.ClaimSystemFactory;
import net.mohron.skyclaims.claim.IClaim;
import net.mohron.skyclaims.claim.IClaimResult;
import net.mohron.skyclaims.claim.IClaimSystem;
import net.mohron.skyclaims.config.type.GlobalConfig;
import net.mohron.skyclaims.island.GenerateIslandTask;
import net.mohron.skyclaims.island.Island;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.biome.BiomeType;
import org.spongepowered.api.world.biome.BiomeTypes;

import java.awt.*;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

public class IslandUtil {
	private static final SkyClaims PLUGIN = SkyClaims.getInstance();
	private static final DataStore GRIEF_PREVENTION_DATA = PLUGIN.getGriefPrevention().dataStore;
	private static final int MAX_ISLAND_SIZE = 512; // 32 x 32 chunks
	private static GlobalConfig config = PLUGIN.getConfig();
	private static int x; // x value of the current region
	private static int z; // y value of the current region
	private static IClaimSystem claimSystem = ClaimSystemFactory.getClaimSystem();

	static {
		// Initialize with stored values before using defaults
		x = 1;
		z = 1;
	}

	public static Island createIsland(Player owner, String schematic) {
		if (config.world.defaultBiome != null) WorldUtil.setRegionBiome(1, 1, BiomeTypes.PLAINS); //TODO Find a way to convert biome names to BiomeTypes & use proper region coords

		IClaimResult claimResult = createIslandClaim(owner.getUniqueId());
		if (!claimResult.getStatus()) PLUGIN.getLogger().info("Failed to create claim");
		return new Island(owner, claimResult.getClaim(), schematic);
	}

	public static boolean hasIsland(UUID owner) {
		return SkyClaims.islands.containsKey(owner);
	}

	public static void saveIsland(Island island) {
		SkyClaims.islands.put(island.getOwner(), island);
	}

	public static Optional<Island> getIsland(UUID owner) {
		return (hasIsland(owner)) ? Optional.of(SkyClaims.islands.get(owner)) : Optional.empty();
	}

	public static Optional<Island> getIslandByLocation(Location<World> location) {
		return getIslandByClaim(GRIEF_PREVENTION_DATA.getClaimAt(location, true, null));
	}

	public static Optional<Island> getIslandByClaim(Claim claim) {
		Island island;
		if (claim.ownerID != null && getIsland(claim.ownerID).isPresent()) {
			island = getIsland(claim.ownerID).get();
			return (island.getClaimId().equals(claim.getID())) ? Optional.of(island) : Optional.empty();
		} else
			return Optional.empty();
	}

	public static void resetIsland(Player owner, String schematic) {
		clearIsland(owner.getUniqueId());
		getIsland(owner.getUniqueId()).ifPresent(island -> {
			GenerateIslandTask generateIsland = new GenerateIslandTask(owner, island, schematic);
			PLUGIN.getGame().getScheduler().createTaskBuilder().execute(generateIsland).submit(PLUGIN);
		});
	}

	private static IClaimResult createIslandClaim(UUID owner) {
		Player player = PLUGIN.getGame().getServer().getPlayer(owner).get();
		IClaimResult createClaimResult;
		createClaimResult = claimSystem.createClaim(
				ConfigUtil.getWorld(),
				new Vector3i(x * 512, x * 512 + MAX_ISLAND_SIZE, 1),
				new Vector3i(255, z * 512, z * 512 + MAX_ISLAND_SIZE),
				UUID.randomUUID(),
				null,
				IClaim.Type.BASIC,
				false,
				player
		);

		return createClaimResult;
	}

	public static void buildIsland(Island island) {
		//TODO Build an "island" in the center of the owners island using a schematic
		int x = island.getCenter().getBlockX();
		int y = ConfigUtil.get(PLUGIN.getConfig().world.defaultHeight, 64);
		int z = island.getCenter().getBlockZ();


		for (int i = -1; i < 1; i++) {
			for (int j = -1; j < 1; j++) {
				//place some dirt
			}
		}
	}

	private static void clearIsland(UUID owner) {
		//TODO Clear island, inventory, enderchest, and supported private mod inventories ie. mod ender chests
	}

	/**
	 * A method to generate a region-scaled spiral pattern and return the x/y pairs of each region
	 *
	 * @return An ArrayList of Points containing the x,y of regions, representing a spiral shape
	 */
	public static ArrayList<Point> generateRegionSpiral() {
		int islandCount = SkyClaims.islands.size();
		int generationSize = (int) Math.sqrt((double) islandCount) + 1;

		ArrayList<Point> coordinates = new ArrayList<Point>(generationSize);
		int[] delta = {0, -1};
		int x = 0;
		int y = 0;

		for (int i = (int) Math.pow(Math.max(generationSize, generationSize), 2); i > 0; i--) {
			if ((-generationSize / 2 < x && x <= generationSize / 2) && (-generationSize / 2 < y && y <= generationSize / 2))
				coordinates.add(new Point(x, y));
			if (x == y || (x < 0 && x == -y) || (x > 0 && x == 1 - y)) {
				// change direction
				delta[0] = -delta[1];
				delta[1] = delta[0];
			}
		}

		return coordinates;
	}

//	private static int getXOffset(int i) {
//		return (i == 1 || i == 3) ? 0 : MAX_ISLAND_SIZE;
//	}

//	private static int getYOffset(int i) {
//		return (i == 0 || i == 1) ? 0 : MAX_ISLAND_SIZE;
//	}
}