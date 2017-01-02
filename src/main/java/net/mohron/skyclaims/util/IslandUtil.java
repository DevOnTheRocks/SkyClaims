package net.mohron.skyclaims.util;

import me.ryanhamshire.griefprevention.DataStore;
import me.ryanhamshire.griefprevention.PlayerData;
import me.ryanhamshire.griefprevention.claim.Claim;
import me.ryanhamshire.griefprevention.claim.CreateClaimResult;
import net.mohron.skyclaims.SkyClaims;
import net.mohron.skyclaims.island.GenerateIslandTask;
import net.mohron.skyclaims.island.Island;
import net.mohron.skyclaims.island.layout.ILayout;
import net.mohron.skyclaims.island.layout.SpiralLayout;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.awt.*;
import java.util.Optional;
import java.util.UUID;

public class IslandUtil {
	private static final SkyClaims PLUGIN = SkyClaims.getInstance();
	private static final DataStore GRIEF_PREVENTION_DATA = PLUGIN.getGriefPrevention().dataStore;
	private static ILayout layout = new SpiralLayout();

	public static Optional<Island> createIsland(Player owner, String schematic) {
		Point region = layout.nextRegion();
		int x = region.x;
		int z = region.y;

		if (ConfigUtil.getDefaultBiome() != null) WorldUtil.setRegionBiome(x, z, ConfigUtil.getDefaultBiome());

		CreateClaimResult claimResult = createProtection(owner, x, z);
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

	private static CreateClaimResult createProtection(Player owner, int rx, int rz) {
		PLUGIN.getLogger().info(String.format(
				"Creating claim for %s with region %s,%s: (%s,%s),(%s,%s)",
				owner.getName(),
				rx, rz,
				rx << 5 << 4, (((rx + 1) << 5) << 4) - 1,
				rz << 5 << 4, (((rz + 1) << 5) << 4) - 1
		));
		DataStore dataStore = PLUGIN.getGriefPrevention().dataStore;
		PlayerData ownerData = dataStore.getOrCreatePlayerData(ConfigUtil.getWorld(), owner.getUniqueId());
		return dataStore.createClaim(
				ConfigUtil.getWorld(),
				rx << 5 << 4,
				(((rx + 1) << 5) << 4) - 1,
				0,
				255,
				rz << 5 << 4,
				(((rz + 1) << 5) << 4) - 1,
				UUID.randomUUID(), null, Claim.Type.BASIC, ownerData.getCuboidMode(), owner);
	}

	private static void clearIsland(UUID owner) {
		//TODO Clear island, inventory, enderchest, and supported private mod inventories ie. mod ender chests
	}
}