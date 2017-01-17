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
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.List;

public class ClaimUtil {
	private static final SkyClaims PLUGIN = SkyClaims.getInstance();
	private static final ClaimManager CLAIM_MANAGER = PLUGIN.getGriefPrevention().getClaimManager(ConfigUtil.getWorld());

	@SuppressWarnings("OptionalGetWithoutIsPresent")
	public static Claim createIslandClaim(User owner, Region region) throws CreateIslandException {
		final int MAX_CLAIM_ATTEMPTS = 10; // limit claim removals to prevent an infinite loop
		int i = 0;
		Claim claim = null;
		ClaimResult claimResult = ClaimUtil.createIslandClaimResult(owner, region);
		do {
			switch (claimResult.getResultType()) {
				case SUCCESS:
					claim = claimResult.getClaim().get();
					CLAIM_MANAGER.addClaim(claim, Cause.source(PLUGIN).build());
					SkyClaims.islandClaims.add(claim);
					PLUGIN.getLogger().info(String.format(
							"Creating claim for %s in region (%s, %s). Claimed from %sx, %sz - %sx, %sz.",
							owner.getName(),
							region.getX(), region.getZ(),
							claim.getLesserBoundaryCorner().getBlockX(), claim.getLesserBoundaryCorner().getBlockZ(),
							claim.getGreaterBoundaryCorner().getBlockX(), claim.getGreaterBoundaryCorner().getBlockZ()
					));
					break;
				case OVERLAPPING_CLAIM:
					CLAIM_MANAGER.deleteClaim(claimResult.getClaim().get(), Cause.source(PLUGIN).build());
					PLUGIN.getLogger().info(String.format("Removing overlapping claim (Owner: %s, ID: %s) while creating %s's island.",
							claimResult.getClaim().get().getOwnerName(),
							claimResult.getClaim().get().getUniqueId(),
							owner.getName()
					));
					break;
				default:
					throw new CreateIslandException(Text.of(TextColors.RED, String.format("Failed to create claim: %s!", claimResult.getResultType())));
			}
			i++;
		} while (claimResult.getResultType() == ClaimResultType.OVERLAPPING_CLAIM && i < MAX_CLAIM_ATTEMPTS);

		if (claim == null)
			throw new CreateIslandException(Text.of(TextColors.RED, String.format("Failed to create claim: %s!", claimResult.getResultType())));

		// Set claim to not expire or be resizable
		claim.getClaimData().setResizable(false);
		claim.getClaimData().setClaimExpiration(false);

		return claim;
	}

	private static ClaimResult createIslandClaimResult(User owner, Region region) {
		int claimRadius = Options.getIntOption(owner.getUniqueId(), Options.INITIAL_SIZE, 32, 8, 255);
		return Claim.builder()
				.world(ConfigUtil.getWorld())
				.bounds(
						new Vector3i(region.getCenterBlock().getX() + claimRadius, 0, region.getCenterBlock().getZ() + claimRadius),
						new Vector3i(region.getCenterBlock().getX() - claimRadius, 255, region.getCenterBlock().getZ() - claimRadius)
				)
				.owner(owner.getUniqueId())
				.type(ClaimType.BASIC)
				.cause(Cause.source(PLUGIN).build())
				.cuboid(false)
				.build();
	}

	public static void createSpawnClaim(List<Region> regions) {
		ClaimResult claimResult = ClaimUtil.createSpawnClaimResult(regions);
		if (claimResult.successful()) {
			PLUGIN.getLogger().info(String.format("Reserved %s regions for spawn. Admin Claim: %s", regions.size(), claimResult.getClaim().get().getUniqueId()));
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
}