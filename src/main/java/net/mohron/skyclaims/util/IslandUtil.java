package net.mohron.skyclaims.util;

import me.ryanhamshire.griefprevention.DataStore;
import me.ryanhamshire.griefprevention.PlayerData;
import me.ryanhamshire.griefprevention.claim.Claim;
import me.ryanhamshire.griefprevention.claim.CreateClaimResult;
import net.mohron.skyclaims.Region;
import net.mohron.skyclaims.SkyClaims;
import net.mohron.skyclaims.island.GenerateIslandTask;
import net.mohron.skyclaims.island.Island;
import net.mohron.skyclaims.island.layout.ILayout;
import net.mohron.skyclaims.island.layout.SpiralLayout;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.Optional;
import java.util.UUID;

public class IslandUtil {
	private static final SkyClaims PLUGIN = SkyClaims.getInstance();
	private static final DataStore GRIEF_PREVENTION_DATA = PLUGIN.getGriefPrevention().dataStore;
	private static ILayout layout = new SpiralLayout();

	public static Optional<Island> createIsland(Player owner, String schematic) {
		Region region = layout.nextRegion();

		if (ConfigUtil.getDefaultBiome().isPresent())
			WorldUtil.setRegionBiome(region, ConfigUtil.getDefaultBiome().get());

		CreateClaimResult claimResult = createProtection(owner, region);
		if (!claimResult.succeeded) {
			PLUGIN.getLogger().error("Failed to create claim. Found overlapping claim: " + claimResult.claim.getID());
			return Optional.empty();
		}
		return Optional.of(new Island(owner, claimResult.claim, schematic));
	}

	public static boolean hasIsland(UUID owner) {
		return SkyClaims.islands.containsKey(owner);
	}

	public static void saveIsland(Island island) {
		SkyClaims.islands.put(island.getOwner(), island);
		PLUGIN.getDatabase().saveIsland(island);
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

	private static CreateClaimResult createProtection(Player owner, Region region) {
		PLUGIN.getLogger().info(String.format(
				"Creating claim for %s with region %s,%s: (%s,%s),(%s,%s)",
				owner.getName(),
				region.getX(), region.getZ(),
				region.getLesserBoundary().getX(), region.getGreaterBoundary().getX(),
				region.getLesserBoundary().getZ(), region.getGreaterBoundary().getZ()
		));
		DataStore dataStore = PLUGIN.getGriefPrevention().dataStore;
		PlayerData ownerData = dataStore.getOrCreatePlayerData(ConfigUtil.getWorld(), owner.getUniqueId());
		return dataStore.createClaim(
				ConfigUtil.getWorld(),
				region.getX() << 5 << 4,
				(((region.getX() + 1) << 5) << 4) - 1,
				0,
				255,
				region.getZ() << 5 << 4,
				(((region.getZ() + 1) << 5) << 4) - 1,
				UUID.randomUUID(), null, Claim.Type.BASIC, ownerData.getCuboidMode(), owner);
	}

	private static void clearIsland(UUID owner) {
		//TODO Clear island, inventory, enderchest, and supported private mod inventories ie. mod ender chests
	}

	public static ILayout getLayout() {
		return layout;
	}
}