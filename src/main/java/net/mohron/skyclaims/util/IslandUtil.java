package net.mohron.skyclaims.util;

import me.ryanhamshire.griefprevention.DataStore;
import me.ryanhamshire.griefprevention.PlayerData;
import me.ryanhamshire.griefprevention.claim.Claim;
import me.ryanhamshire.griefprevention.claim.CreateClaimResult;
import net.mohron.skyclaims.Region;
import net.mohron.skyclaims.SkyClaims;
import net.mohron.skyclaims.island.Island;
import net.mohron.skyclaims.island.RegenerateRegionTask;
import net.mohron.skyclaims.island.layout.ILayout;
import net.mohron.skyclaims.island.layout.SpiralLayout;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
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

		CreateClaimResult claimResult = forceCreateProtection(owner, region);
		//CreateClaimResult claimResult = createProtection(owner, region);
		if (!claimResult.succeeded) {
			PLUGIN.getLogger().error("Failed to create claim. Found overlapping claim: " + claimResult.claim.getID());
			return Optional.empty();
		}
		return Optional.of(new Island(owner, claimResult.claim, region, schematic));
	}

	private static CreateClaimResult forceCreateProtection(Player owner, Region region) {
		CreateClaimResult claimResult = null;
		while (claimResult == null || !claimResult.succeeded) {
			claimResult = createProtection(owner, region);
			if (!claimResult.succeeded) {
				PLUGIN.getLogger().error("Failed to create claim. Found overlapping claim: " + claimResult.claim.getID() + " removing overlapping claim.");
				PLUGIN.getGriefPrevention().dataStore.deleteClaim(claimResult.claim);
			}
		}
		return claimResult;
	}

	public static boolean hasIsland(UUID owner) {
		return SkyClaims.islands.containsKey(owner);
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

	public static void resetIsland(User owner, String schematic) {
		// Send online players to spawn!
		owner.getPlayer().ifPresent(
				player -> CommandUtil.createForceTeleportConsumer(player, WorldUtil.getDefaultWorld().getSpawnLocation())
		);
		// Destroy everything they ever loved!
		getIsland(owner.getUniqueId()).ifPresent(island -> {
			RegenerateRegionTask regenerateRegionTask = new RegenerateRegionTask(owner, island, schematic);
			PLUGIN.getGame().getScheduler().createTaskBuilder().execute(regenerateRegionTask).submit(PLUGIN);
		});
	}

	public static CreateClaimResult createProtection(Player owner, Region region) {
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
				region.getLesserBoundary().getX(),
				region.getGreaterBoundary().getX(),
				0,
				255,
				region.getLesserBoundary().getZ(),
				region.getGreaterBoundary().getZ(),
				UUID.randomUUID(), null, Claim.Type.BASIC, ownerData.getCuboidMode(), owner);
	}
}