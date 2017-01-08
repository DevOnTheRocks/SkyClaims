package net.mohron.skyclaims.util;

import com.flowpowered.math.vector.Vector3i;
import me.ryanhamshire.griefprevention.api.claim.Claim;
import me.ryanhamshire.griefprevention.api.claim.ClaimResult;
import me.ryanhamshire.griefprevention.api.claim.ClaimType;
import net.mohron.skyclaims.SkyClaims;
import net.mohron.skyclaims.world.region.Region;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.text.Text;

import java.util.List;

public class ClaimUtil {
	private static final SkyClaims PLUGIN = SkyClaims.getInstance();

	public static ClaimResult createIslandClaim(User owner, Region region) {
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

	public static void setEntryFlag(Claim claim, boolean entry) {
		// Set ENTER_CLAIM flag in the claim supplied to the defined value
	}
}