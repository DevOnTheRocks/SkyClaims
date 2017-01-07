package net.mohron.skyclaims.util;

import com.flowpowered.math.vector.Vector3i;
import me.ryanhamshire.griefprevention.api.claim.Claim;
import me.ryanhamshire.griefprevention.api.claim.ClaimResult;
import me.ryanhamshire.griefprevention.api.claim.ClaimType;
import net.mohron.skyclaims.SkyClaims;
import net.mohron.skyclaims.world.Island;
import net.mohron.skyclaims.world.region.Region;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.text.Text;

import java.util.List;
import java.util.UUID;

public class ClaimUtil {
	private static final SkyClaims PLUGIN = SkyClaims.getInstance();

	public static ClaimResult createIslandClaim(User owner, Region region) {
		PLUGIN.getLogger().info(String.format(
				"Creating claim for %s with region (%s, %s): (%s, %s), (%s, %s)",
				owner.getName(),
				region.getX(), region.getZ(),
				region.getLesserBoundary().getX(), region.getLesserBoundary().getZ(),
				region.getGreaterBoundary().getX(), region.getGreaterBoundary().getZ()
		));

		return Claim.builder()
				.world(ConfigUtil.getWorld())
				.bounds(
						new Vector3i(region.getLesserBoundary().getX(), 0, region.getLesserBoundary().getZ()),
						new Vector3i(region.getGreaterBoundary().getX(), 255, region.getGreaterBoundary().getZ())
				)
				.owner(owner.getUniqueId())
				.type(ClaimType.BASIC)
				.cause(Cause.source(PLUGIN).build())
				.cuboid(false)
				.build();
	}

	public static ClaimResult createSpawnClaim(List<Region> regions) {
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

	public static boolean isIslandClaim(Claim claim) {
		return isIslandClaim(claim.getUniqueId());
	}

	public static boolean isIslandClaim(UUID uuid) {
		for (Island island : SkyClaims.islands.values()) {
			if (island.getClaim().getUniqueId().equals(uuid)) return true;
		}
		return false;
	}

	public void setClaimSettings(User user, Claim claim) {
		claim.getClaimData().setResizable(false);
		claim.getClaimData().setClaimExpiration(false);
		claim.getClaimData().setName(Text.of(user.getName(), "'s Island"));
	}
}