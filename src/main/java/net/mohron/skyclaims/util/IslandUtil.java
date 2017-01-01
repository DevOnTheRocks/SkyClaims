package net.mohron.skyclaims.util;

import com.flowpowered.math.vector.Vector3i;
import me.ryanhamshire.griefprevention.DataStore;
import me.ryanhamshire.griefprevention.claim.Claim;
import me.ryanhamshire.griefprevention.claim.CreateClaimResult;
import net.mohron.skyclaims.SkyClaims;
import net.mohron.skyclaims.claim.ClaimSystemFactory;
import net.mohron.skyclaims.claim.IClaim;
import net.mohron.skyclaims.claim.IClaimResult;
import net.mohron.skyclaims.claim.IClaimSystem;
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
	private static final int MAX_ISLAND_SIZE = 511; // 32 x 32 chunks
	private static IClaimSystem claimSystem = ClaimSystemFactory.getClaimSystem();
	private static ILayout layout = new SpiralLayout();

	public static Island createIsland(Player owner, String schematic) {
		Point region = layout.nextRegion();
		int x = region.x;
		int z = region.y;

		if (ConfigUtil.getDefaultBiome() != null) WorldUtil.setRegionBiome(x, z, ConfigUtil.getDefaultBiome());

		CreateClaimResult claimResult = createIslandClaimLegacy(owner, x, z);
		if (!claimResult.succeeded) PLUGIN.getLogger().error("Failed to create claim");
		return new Island(owner, claimResult.claim, schematic);
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

	private static CreateClaimResult createIslandClaimLegacy(Player player, int rx, int rz) {
		return PLUGIN.getGriefPrevention().dataStore.createClaim(
				ConfigUtil.getWorld(),
				rx >> 5 >> 4,
				rx >> 5 >> 4 + MAX_ISLAND_SIZE,
				0,
				255,
				rz >> 5 >> 4,
				rz >> 5 >> 4 + MAX_ISLAND_SIZE,
				UUID.randomUUID(),
				null,
				Claim.Type.BASIC,
				false,
				player
		);
	}

	private static IClaimResult createIslandClaim(UUID owner, int rx, int rz) {
		Player player = PLUGIN.getGame().getServer().getPlayer(owner).get();
		IClaimResult createClaimResult;
		createClaimResult = claimSystem.createClaim(
				ConfigUtil.getWorld(),
				new Vector3i(rx * 512, rx * 512 + MAX_ISLAND_SIZE, 1),
				new Vector3i(255, rz * 512, rz * 512 + MAX_ISLAND_SIZE),
				UUID.randomUUID(),
				null,
				IClaim.Type.BASIC,
				false,
				player
		);

		return createClaimResult;
	}

	private static void clearIsland(UUID owner) {
		//TODO Clear island, inventory, enderchest, and supported private mod inventories ie. mod ender chests
	}
}