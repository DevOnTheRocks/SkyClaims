package net.mohron.skyclaims.util;

import com.flowpowered.math.vector.Vector3i;
import me.ryanhamshire.griefprevention.api.claim.Claim;
import me.ryanhamshire.griefprevention.api.claim.ClaimManager;
import me.ryanhamshire.griefprevention.api.claim.ClaimResult;
import me.ryanhamshire.griefprevention.api.claim.ClaimResultType;
import me.ryanhamshire.griefprevention.api.claim.ClaimType;
import net.mohron.skyclaims.SkyClaims;
import net.mohron.skyclaims.exception.CreateIslandException;
import net.mohron.skyclaims.permissions.Options;
import net.mohron.skyclaims.world.region.Region;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.service.user.UserStorageService;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class ClaimUtil {
	private static final SkyClaims PLUGIN = SkyClaims.getInstance();
	private static final ClaimManager CLAIM_MANAGER = PLUGIN.getGriefPrevention().getClaimManager(ConfigUtil.getWorld());

	@SuppressWarnings("OptionalGetWithoutIsPresent")
	public static Claim createIslandClaim(UUID ownerUniqueId, Region region) throws CreateIslandException {
		final int MAX_CLAIM_ATTEMPTS = 10; // limit claim removals to prevent an infinite loop
		int i = 0;
		Claim claim = null;
		ClaimResult claimResult;
		do {
			claimResult = ClaimUtil.createIslandClaimResult(ownerUniqueId, region);
			switch (claimResult.getResultType()) {
				case SUCCESS:
					claim = claimResult.getClaim().get();
					CLAIM_MANAGER.addClaim(claim, Cause.source(PLUGIN).build());
					PLUGIN.getLogger().debug(String.format(
							"Creating %s's claim in region (%s, %s). Claimed from %sx, %sz - %sx, %sz.",
							getName(ownerUniqueId),
							region.getX(), region.getZ(),
							claim.getLesserBoundaryCorner().getBlockX(), claim.getLesserBoundaryCorner().getBlockZ(),
							claim.getGreaterBoundaryCorner().getBlockX(), claim.getGreaterBoundaryCorner().getBlockZ()
					));

					claim.getData().setResizable(false);
					claim.getData().setClaimExpiration(false);
					claim.getData().setRequiresClaimBlocks(false);
					break;
				case OVERLAPPING_CLAIM:
					for (Claim claim1 : claimResult.getClaims()) {
						CLAIM_MANAGER.deleteClaim(claim1, Cause.source(PLUGIN).build());
					}
					PLUGIN.getLogger().info(String.format("Removing claim overlapping %s's island (Owner: %s, ID: %s).",
							getName(ownerUniqueId),
							claimResult.getClaim().get().getOwnerName(),
							claimResult.getClaim().get().getUniqueId()
					));
					break;
				default:
					throw new CreateIslandException(Text.of(TextColors.RED, String.format("Failed to create claim: %s!", claimResult.getResultType())));
			}
			i++;
		} while (claimResult.getResultType() == ClaimResultType.OVERLAPPING_CLAIM && i < MAX_CLAIM_ATTEMPTS);

		if (claim == null)
			throw new CreateIslandException(Text.of(TextColors.RED, String.format("Failed to create claim: %s!", claimResult.getResultType())));

		return claim;
	}

	private static ClaimResult createIslandClaimResult(UUID ownerUniqueId, Region region) {
		int claimRadius = Options.getIntOption(ownerUniqueId, Options.INITIAL_SIZE, 32, 8, 255);
		return Claim.builder()
				.world(ConfigUtil.getWorld())
				.bounds(
						new Vector3i(region.getCenter().getBlockX() + claimRadius, 0, region.getCenter().getBlockZ() + claimRadius),
						new Vector3i(region.getCenter().getBlockX() - claimRadius, 255, region.getCenter().getBlockZ() - claimRadius)
				)
				.owner(ownerUniqueId)
				.type(ClaimType.BASIC)
				.requiresClaimBlocks(false)
				.cause(Cause.source(PLUGIN).build())
				.cuboid(false)
				.build();
	}

	@SuppressWarnings("OptionalGetWithoutIsPresent")
	public static void createSpawnClaim(List<Region> regions) {
		ClaimResult claimResult = ClaimUtil.createSpawnClaimResult(regions);
		if (claimResult.successful()) {
			PLUGIN.getLogger().debug(String.format("Reserved %s regions for spawn. Admin Claim: %s", regions.size(), claimResult.getClaim().get().getUniqueId()));
			CLAIM_MANAGER.addClaim(claimResult.getClaim().get(), Cause.source(PLUGIN).build());
		}
	}

	private static ClaimResult createSpawnClaimResult(List<Region> regions) {
		Region lesserRegion = new Region(0, 0);
		Region greaterRegion = new Region(0, 0);
		for (Region region : regions) {
			if (region.getX() == region.getZ()) {
				if (region.getX() < lesserRegion.getX()) lesserRegion = region;
				if (region.getX() < greaterRegion.getX()) greaterRegion = region;
			}
		}

		return Claim.builder()
				.world(ConfigUtil.getWorld())
				.bounds(
						new Vector3i(lesserRegion.getLesserBoundary().getX(), 0, lesserRegion.getLesserBoundary().getZ()),
						new Vector3i(greaterRegion.getGreaterBoundary().getX(), 255, greaterRegion.getGreaterBoundary().getZ())
				)
				.type(ClaimType.ADMIN)
				.cause(Cause.source(PLUGIN).build())
				.cuboid(false)
				.build();
	}

	public static void setEntryFlag(Claim claim, boolean entry) {
		// Set ENTER_CLAIM flag in the claim supplied to the defined value
	}

	@SuppressWarnings("OptionalGetWithoutIsPresent")
	private static String getName(UUID uuid) {
		Optional<User> user = PLUGIN.getGame().getServiceManager().provide(UserStorageService.class).get().get(uuid);
		if (user.isPresent()) {
			return user.get().getName();
		} else {
			try {
				return PLUGIN.getGame().getServer().getGameProfileManager().get(uuid).get().getName().get();
			} catch (Exception e) {
				return "somebody";
			}
		}
	}
}