package net.mohron.skyclaims.util;

import me.ryanhamshire.griefprevention.api.claim.Claim;
import me.ryanhamshire.griefprevention.api.claim.ClaimManager;
import me.ryanhamshire.griefprevention.api.claim.ClaimResult;
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
	private static final ClaimManager CLAIM_MANAGER = PLUGIN.getGriefPrevention().getClaimManager(WorldUtil.getDefaultWorld());
	private static ILayout layout = new SpiralLayout();

	public static Optional<Island> createIsland(Player owner, String schematic) {
		Region region = layout.nextRegion();

		if (ConfigUtil.getDefaultBiome().isPresent())
			WorldUtil.setRegionBiome(region, ConfigUtil.getDefaultBiome().get());

		ClaimResult claimResult = ClaimUtil.createIslandClaim(owner, region);
		if (!claimResult.successful()) {
			PLUGIN.getLogger().error("Failed to create claim. Found overlapping claim: " + claimResult.getClaim().getOwnerUniqueId());
			return Optional.empty();
		}
		CLAIM_MANAGER.addClaim(claimResult.getClaim());
		return Optional.of(new Island(owner, claimResult.getClaim(), schematic));
	}

	public static boolean hasIsland(UUID owner) {
		return SkyClaims.islands.containsKey(owner);
	}

	public static Optional<Island> getIsland(UUID owner) {
		return (hasIsland(owner)) ? Optional.of(SkyClaims.islands.get(owner)) : Optional.empty();
	}

	public static Optional<Island> getIslandByLocation(Location<World> location) {
		return getIslandByClaim(CLAIM_MANAGER.getClaimAt(location, true));
	}

	private static Optional<Island> getIslandByClaim(Claim claim) {
		Island island;
		if (claim.getOwnerUniqueId() != null && getIsland(claim.getOwnerUniqueId()).isPresent()) {
			island = getIsland(claim.getOwnerUniqueId()).get();
			return (island.getClaimId().equals(claim.getUniqueId())) ? Optional.of(island) : Optional.empty();
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
}