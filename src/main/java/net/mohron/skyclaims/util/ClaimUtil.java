package net.mohron.skyclaims.util;

import com.flowpowered.math.vector.Vector3i;
import me.ryanhamshire.griefprevention.api.claim.Claim;
import me.ryanhamshire.griefprevention.api.claim.ClaimResult;
import me.ryanhamshire.griefprevention.api.claim.ClaimType;
import net.mohron.skyclaims.Region;
import net.mohron.skyclaims.SkyClaims;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.text.Text;

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
				.cuboid(false)
				.build();
	}

	public void setClaimSettings(User user, Claim claim) {
		claim.getClaimData().setResizable(false);
		claim.getClaimData().setName(Text.of(user.getName(), "'s Island"));

	}
}