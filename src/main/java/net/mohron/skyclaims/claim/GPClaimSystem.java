package net.mohron.skyclaims.claim;

import com.flowpowered.math.vector.Vector3i;
import me.ryanhamshire.griefprevention.api.claim.Claim;
import me.ryanhamshire.griefprevention.api.claim.ClaimManager;
import me.ryanhamshire.griefprevention.api.claim.ClaimResult;
import me.ryanhamshire.griefprevention.api.claim.ClaimType;
import net.mohron.skyclaims.SkyClaims;
import net.mohron.skyclaims.util.ConfigUtil;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.world.World;

import java.util.Optional;
import java.util.UUID;

public class GPClaimSystem implements IClaimSystem {

	ClaimManager claimManager = SkyClaims.getInstance().getGriefPrevention().getClaimManager(ConfigUtil.getWorld());

	public GPClaimSystem() {

	}

	public ClaimResult createClaim(World world, Vector3i a, Vector3i b, UUID claimId, Claim parent, ClaimType claimType, boolean cuboid, Player player) {
		return Claim.builder()
				.world(ConfigUtil.getWorld())
				.bounds(a, b)
				.owner(player.getUniqueId())
				.type(claimType)
				.cause(Cause.source(SkyClaims.getInstance()).build())
				.cuboid(cuboid)
				.build();
	}

	public Optional<Claim> getClaim(UUID claimId) {
		return claimManager.getClaimByUUID(claimId);
	}
}
