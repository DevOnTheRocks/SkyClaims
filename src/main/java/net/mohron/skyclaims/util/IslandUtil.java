package net.mohron.skyclaims.util;

import me.ryanhamshire.griefprevention.api.claim.Claim;
import me.ryanhamshire.griefprevention.api.claim.ClaimManager;
import me.ryanhamshire.griefprevention.api.claim.ClaimResult;
import net.mohron.skyclaims.SkyClaims;
import net.mohron.skyclaims.world.Island;
import net.mohron.skyclaims.world.RegenerateRegionTask;
import net.mohron.skyclaims.world.region.IRegionPattern;
import net.mohron.skyclaims.world.region.Region;
import net.mohron.skyclaims.world.region.SpiralRegionPattern;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.Optional;
import java.util.UUID;

public class IslandUtil {
	private static final SkyClaims PLUGIN = SkyClaims.getInstance();
	private static final ClaimManager CLAIM_MANAGER = PLUGIN.getGriefPrevention().getClaimManager(WorldUtil.getDefaultWorld());
	private static IRegionPattern layout = new SpiralRegionPattern();

	public static boolean hasIsland(UUID owner) {
		return SkyClaims.islands.containsKey(owner);
	}

	@SuppressWarnings("OptionalGetWithoutIsPresent")
	public static Optional<Island> createIsland(Player owner, String schematic) {
		Region region = layout.nextRegion();

		if (ConfigUtil.getDefaultBiome().isPresent())
			WorldUtil.setRegionBiome(region, ConfigUtil.getDefaultBiome().get());

		ConfigUtil.getCreateCommands().ifPresent(commands -> {
			for (String command : commands) {
				PLUGIN.getGame().getCommandManager().process(PLUGIN.getGame().getServer().getConsole(), command.replace("@p", owner.getName()));
			}
		});

		ClaimResult claimResult = ClaimUtil.createIslandClaim(owner, region);
		switch (claimResult.getResultType()) {
			case CLAIM_ALREADY_EXISTS:
				PLUGIN.getLogger().error("Failed to create claim: claim already exists!");
				break;
			case CLAIM_NOT_FOUND:
				PLUGIN.getLogger().error("Failed to create claim: claim not found!");
				break;
			case CLAIMS_DISABLED:
				PLUGIN.getLogger().error("Failed to create claim: claims are disabled!");
				break;
			case EVENT_CANCELLED:
				PLUGIN.getLogger().error("Failed to create claim: create claim event was cancelled!");
				break;
			case OVERLAPPING_CLAIM:
				PLUGIN.getLogger().error("Failed to create claim: found overlapping claim!");
				break;
			case PARENT_CLAIM_MISMATCH:
				PLUGIN.getLogger().error("Failed to create claim: parent claim mismatch!");
				break;
			case WRONG_CLAIM_TYPE:
				PLUGIN.getLogger().error("Failed to create claim: wrong claim type!");
				break;
			case SUCCESS:
				CLAIM_MANAGER.addClaim(claimResult.getClaim().get(), Cause.source(PLUGIN).build());
				return Optional.of(new Island(owner, claimResult.getClaim().get(), schematic));
		}

		return Optional.empty();
	}

	public static void resetIsland(User owner, String schematic) {
		// Send online players to spawn!
		owner.getPlayer().ifPresent(
				player -> CommandUtil.createForceTeleportConsumer(player, WorldUtil.getDefaultWorld().getSpawnLocation())
		);
		// Run reset commands
		ConfigUtil.getResetCommands().ifPresent(commands -> {
			for (String command : commands) {
				PLUGIN.getGame().getCommandManager().process(PLUGIN.getGame().getServer().getConsole(), command.replace("@p", owner.getName()));
			}
		});
		// Destroy everything they ever loved!
		getIsland(owner.getUniqueId()).ifPresent(island -> {
			RegenerateRegionTask regenerateRegionTask = new RegenerateRegionTask(owner, island, schematic);
			PLUGIN.getGame().getScheduler().createTaskBuilder().execute(regenerateRegionTask).submit(PLUGIN);
		});
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
			return (island.getClaimUniqueId().equals(claim.getUniqueId())) ? Optional.of(island) : Optional.empty();
		} else
			return Optional.empty();
	}
}